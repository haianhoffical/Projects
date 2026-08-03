# DHV E-STUDENT

Sàn thương mại điện tử dành riêng cho sinh viên Đại học Hùng Vương TP.HCM.

## Cấu trúc dự án

```
DHV-E-STUDENT/
├── frontend/               # Phiên bản HTML standalone
│   ├── index.html
│   ├── css/
│   ├── js/
│   └── images/
├── backend/                # Spring Boot Full-Stack
│   ├── pom.xml
│   └── src/main/java/com/dhvestudent/
│       ├── config/         # Security, Web, WebSocket
│       ├── controller/     # REST API + Page controllers
│       ├── dto/            # Data Transfer Objects
│       ├── entity/         # JPA Entities (21 tables)
│       ├── repository/     # Spring Data JPA
│       ├── security/       # JWT, Filter, UserDetails
│       └── service/        # Business logic
│   └── src/main/resources/
│       ├── application.properties
│       ├── static/         # CSS, JS, Images
│       └── templates/      # Thymeleaf HTML
├── database/
│   └── database.sql        # MySQL schema + seed data
├── uploads/                # Thư mục upload file
└── README.md
```

## Công nghệ

- **Frontend:** HTML5, CSS3, JavaScript, Font Awesome 6, Thymeleaf
- **Backend:** Spring Boot 3.2, Spring Security, Spring Data JPA, Spring WebSocket
- **Database:** MySQL 8.0
- **Auth:** JWT (JSON Web Token)
- **Payment:** MoMo Sandbox, ZaloPay Sandbox
- **Build:** Maven

## Hướng dẫn chạy

### 1. Database
```bash
mysql -u root -p < database/database.sql
```

### 2. Backend
```bash
cd backend
# Cập nhật application.properties với thông tin MySQL của bạn
mvn spring-boot:run
```

### 3. Truy cập
- Website: http://localhost:8080
- API: http://localhost:8080/api/

## Tài khoản mặc định

Sau khi chạy `database.sql`, tạo tài khoản qua giao diện đăng ký với email `@dhv.edu.vn`.

## Bảo mật

- BCrypt password hashing
- JWT authentication
- SQL Injection prevention (JPA parameterized queries)
- XSS protection (Thymeleaf auto-escape)
- CSRF protection (Spring Security)
- Role-based access control

## Tác giả

Đề tài NCKH – Nguyễn Thị Hải Anh
Trường Đại học Hùng Vương TP.HCM
