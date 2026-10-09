# Testing

## Kết quả ngày 2026-10-09
- Backend: **11 tests passed**, không skip; PostgreSQL 17 thật qua Testcontainers/Podman.
- Frontend: **15 tests passed** qua Vitest, Vue Test Utils và jsdom.
- `npm run build` (bao gồm type-check), `npm run lint`, `git diff --check`: thành công.
- Chưa chạy thao tác end-to-end bằng trình duyệt thật; API và component được kiểm tra riêng.

| Phạm vi | Kiểm tra | Test |
|---|---|---|
| Auth | Đăng ký, hash mật khẩu, trùng username, input không hợp lệ, sai mật khẩu | AccountTaskIntegrationTest |
| Session / CSRF | Token thực từ endpoint, đổi session id lúc login, vô hiệu token cũ, logout, 401, thiếu CSRF → 403 | AccountTaskIntegrationTest |
| Task API / DB | CRUD, lọc ngày, đổi ngày, giữ trạng thái hoàn thành khi sửa, 400/404 | AccountTaskIntegrationTest |
| Quyền truy cập | Tài khoản B không xem/sửa/hoàn thành/xóa task của A | AccountTaskIntegrationTest |
| Business logic | Trim tiêu đề, mặc định chưa hoàn thành, hoàn thành lặp lại và đảo ngược | TaskServiceTest |
| Database | Flyway migration, Hibernate schema validation, application context | LifeosApplicationTests |
| API errors | ProblemDetail 404 và field validation 400 | GlobalExceptionHandlerTest |
| HTTP client | Cookie, CSRF, form login, JSON, 204, field errors, hết session | http.spec.ts |
| Giao diện auth | Đăng ký, đăng nhập thành công, sai thông tin đăng nhập | AuthView.spec.ts |
| Giao diện task | Tạo, hoàn thành, xác nhận xóa, giữ input khi lỗi, bỏ kết quả tải cũ | HomeView.spec.ts |
| Layout | Brand và slot | AppLayout.spec.ts |

## Chạy lại
```bash
cd backend
DOCKER_HOST=unix:///run/user/$UID/podman/podman.sock TESTCONTAINERS_RYUK_DISABLED=true ./mvnw test

cd ../frontend
npm run test:unit -- --run
npm run build
npm run lint
```

Socket Podman phải tồn tại và truy cập được. Trong phiên kiểm thử này, socket mặc định không tồn tại dù systemd báo active; đã dùng `podman system service --time=0 unix:///tmp/lifeos-test-podman.sock` và đổi `DOCKER_HOST` tương ứng. Testcontainers dùng database tạm, không dùng dữ liệu `lifeos-postgres` của môi trường dev.

## Kiểm tra thủ công
1. Đăng ký rồi đăng nhập; tải lại trang vẫn giữ phiên.
2. Tạo task, sửa tiêu đề, chuyển sang ngày khác, hoàn thành và bỏ hoàn thành.
3. Xóa: hủy xác nhận trước, sau đó xác nhận xóa.
4. Đăng xuất, đăng ký tài khoản thứ hai; danh sách công việc riêng biệt.
5. Tắt backend, thử lưu: hiện lỗi và giữ nội dung nhập; bật lại và đăng nhập lại.
