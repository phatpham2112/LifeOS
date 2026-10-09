# LifeOS — Project Overview

## Mục tiêu
Xây dựng ứng dụng quản lý cuộc sống cá nhân, đồng thời thực hành Java Backend và kỹ thuật phần mềm.

## MVP
- Quản lý công việc hằng ngày
- Theo dõi thói quen
- Nhật ký ngắn và review tuần

## Tech stack (điều chỉnh khi cần)
- Backend: Java, Spring Boot, PostgreSQL
- Frontend: Vue 3, TypeScript
- Dev: Git, Docker, automated tests

## Cấu trúc tài liệu
- `docs/FEATURES.md`: tính năng và tiêu chí hoàn thành
- `docs/DESIGN.md`: quyết định và luồng thiết kế đáng ghi nhớ
- `docs/TESTING.md`: kiểm thử quan trọng
- `docs/LEARNINGS.md`: bài học kỹ thuật
- `notion/SETUP.md`: hướng dẫn quản lý task bằng Notion
- `templates/`: mẫu có thể sao chép

## Quy trình gọn
Chọn feature → viết acceptance criteria → thiết kế vừa đủ → code + test → cập nhật docs.

**Nguyên tắc:** Không cần điền tất cả tài liệu trước khi code. Chỉ ghi khi có ích cho triển khai hoặc học tập.

## Chạy backend (local)
```bash
# 1. Database
cp infra/.env.example infra/.env   # đổi POSTGRES_PASSWORD
podman compose --env-file infra/.env -f infra/compose.yaml up -d

# 2. Backend (cần Java 21)
cd backend
./mvnw spring-boot:run
# Health check: http://localhost:8080/actuator/health

# 3. Test (Testcontainers chạy trên podman)
DOCKER_HOST=unix:///run/user/$UID/podman/podman.sock TESTCONTAINERS_RYUK_DISABLED=true ./mvnw test
```

Backend tự đọc `infra/.env` khi working directory là thư mục gốc LifeOS hoặc `backend`
(áp dụng cả IntelliJ Run). Thêm cấu hình theo dạng `KEY=value`, không bọc value trong
dấu nháy và không dùng `export`. Biến môi trường của tiến trình được ưu tiên hơn file này.
Các key mới có thể được tham chiếu trong `application.yaml` bằng `${KEY}`.
File được nạp khi khởi động; restart backend sau khi sửa.

## Chạy frontend (local)
```bash
cd frontend
npm install
npm run dev          # http://localhost:5173, /api được proxy sang backend :8080
npm run test:unit    # Vitest
npm run lint         # oxlint + ESLint
npm run build        # type-check + build ra dist/
```

## Tính năng đầu tiên
Sau khi chạy database, backend và frontend, mở `http://localhost:5173`:
1. Chọn **Đăng ký**, tạo tên đăng nhập và mật khẩu (tối thiểu 8 ký tự).
2. Đăng nhập, chọn ngày và thêm công việc.
3. Đánh dấu hoàn thành, sửa tiêu đề/ngày hoặc xóa công việc.
4. Đăng xuất và thử tài khoản khác: mỗi tài khoản có danh sách riêng.

Flyway tự tạo bảng tài khoản và công việc khi backend khởi động. Session hiện lưu trong bộ nhớ backend, nên cần đăng nhập lại sau khi backend restart. Redis chưa tham gia luồng đăng nhập.

Khi chạy qua HTTPS, đặt `SESSION_COOKIE_SECURE=true`; frontend và `/api` cần được phục vụ cùng origin (reverse proxy).
