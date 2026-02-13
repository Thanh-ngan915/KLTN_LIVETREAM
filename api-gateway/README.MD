Bài 1
Holy_Dev edited this page on Oct 23, 2025 · 1 revision
Video : https://youtu.be/p6c18t-zDNc
Kế hoạch tạo Microservices với Java Spring Boot
Mình sẽ hướng dẫn bạn từng bước một, không cần phải đọc document dài dòng. Mình sẽ giúp bạn làm project thật sự, chạy được ngay trong IntelliJ Ultimate. Từ đó, bạn sẽ tự hiểu dần microservices là gì và Java Spring Boot hoạt động thế nào.

Những gì bạn sẽ làm:
Chúng ta sẽ làm một hệ thống microservices đơn giản gồm:

3 Microservices:
User Service

Lưu thông tin người dùng: id, tên, email

REST API: tạo user, lấy user

Order Service

Lưu đơn hàng: id, userId, sản phẩm, giá

REST API: tạo đơn hàng, lấy đơn hàng

API Gateway (Spring Cloud Gateway)

Là cửa chính cho toàn bộ hệ thống

Route request đến User Service và Order Service.

Bắt đầu:
Bước 1: Cài đặt môi trường
IntelliJ Ultimate (bạn đã có)

Java 17 (hoặc Java 21)

Pseudocode từng bước
Tạo User Service

Spring Boot App tên user-service

Thêm các dependency: Spring Web, Spring Data JPA, H2 (database in-memory), Lombok

Tạo entity User, repository, controller

Tạo Order Service

Spring Boot App tên order-service

Dependency tương tự như User Service

Tạo entity Order, repository, controller

Tạo API Gateway

Spring Boot App tên api-gateway

Thêm dependency: Spring Cloud Gateway, Eureka Client

Cấu hình route các request đến user-service và order-service

Thêm Eureka Discovery Server

Để các service tự động đăng ký và tìm nhau
Test các API

Gửi request đến API Gateway, nó sẽ định tuyến đến User/Order service
Mục tiêu đầu tiên: Tạo user-service
Bước 1: Tạo Project Spring Boot trong IntelliJ Ultimate
Cách làm:
Mở IntelliJ Ultimate → Chọn File > New > Project...

Trong danh sách chọn Spring Initializr ➜ Next

Project SDK: chọn Java 17 hoặc 21 (cài sẵn nếu chưa có)

Group: com.example
Artifact: user-service
➜ Next

Dependencies:

Spring Web

Spring Data JPA

H2 Database

Lombok
➜ Next ➜ Finish

IntelliJ sẽ tạo project user-service sẵn cho bạn.

Bước 2: Tạo class User (entity)
// src/main/java/com/example/userservice/model/User.java
package com.example.userservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") // cho ai chưa biết thì users nhớ thêm s nhé, chứ ko thêm s la thanh user keyword trong Sql và khi chay code se bi loi
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    private String name;
    private String email;
}
Bước 3: Tạo UserRepository
// src/main/java/com/example/userservice/repository/UserRepository.java
package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
Bước 4: Tạo UserController
// src/main/java/com/example/userservice/controller/UserController.java
package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
Bước 5: Cấu hình application.yml
# src/main/resources/application.yml
server:
port: 8081

spring:
datasource:
url: jdbc:h2:mem:userdb
driverClassName: org.h2.Driver
username: sa
password:
h2:
console:
enabled: true
jpa:
hibernate:
ddl-auto: update
show-sql: true
Bước 6: Chạy thử
Run UserServiceApplication.java trong IntelliJ

Truy cập:

POST http://localhost:8081/api/users

GET http://localhost:8081/api/users

H2 Console: http://localhost:8081/h2-console

JDBC URL: jdbc:h2:mem:userdb
Vậy là bạn đã hoàn thành microservice đầu tiên 🎉

Note: cho bác nào chưa biết dữ liệu sẽ được lưu hay được chuyển đi đâu

Bạn đang dùng H2 database (in-memory) nên dữ liệu không lưu vào file nào, mà chỉ tồn tại trong RAM khi app đang chạy. Khi bạn tắt app, dữ liệu mất.

Cách xem dữ liệu
Spring Boot + H2 đã bật sẵn H2 Console – một giao diện web giống phpMyAdmin:

👉 Truy cập H2 Console:
Mở trình duyệt:
http://localhost:8081/h2-console

Điền thông tin:

Trường	Giá trị
JDBC URL	jdbc:h2:mem:userdb
Username	sa
Password	(để trống)
➜ Connect
Bảng users sẽ hiện ra
Bạn có thể bấm vào bảng USERS, rồi chạy câu SQL:

SELECT * FROM USERS;
➡️ Sẽ thấy tất cả user đã lưu trong bảng.

Lưu ý:
H2 mặc định sẽ xóa sạch dữ liệu mỗi khi restart app (vì bạn dùng jdbc:h2:mem:userdb)

Nếu muốn lưu ra file, đổi JDBC URL thành:

spring.datasource.url: jdbc:h2:file:./data/userdb
→ Khi đó dữ liệu sẽ lưu tại user-service/data/userdb.mv.db