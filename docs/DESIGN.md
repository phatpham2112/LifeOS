# Technical Design

## Kiến trúc
- Monolith Spring Boot 4, Java 21, package-by-feature trong `com.phatpham.lifeos`.
- Các module: `common`, `auth`, `task`; controller → service → repository → entity.
- Module task gọi `AccountService` để lấy chủ sở hữu; không gọi repository của module auth.
- PostgreSQL 17; Flyway quản lý schema, Hibernate chỉ `validate`.
- Vue 3 + TypeScript + Vite, Router và Pinia. Dev proxy `/api` → backend `:8080`.
- Lỗi API dùng ProblemDetail; `http.ts` chuyển thành `ApiError` và thông báo khi phiên hết hạn.

## Tài khoản và session
- Tên đăng nhập cố định, chỉ chữ thường/số/`_`, duy nhất trong database.
- BCrypt hash mật khẩu; tối thiểu 8 ký tự, tối đa 72 byte UTF-8.
- Spring Security form login (`application/x-www-form-urlencoded`); session fixation protection và logout do framework xử lý.
- Cookie session HttpOnly, SameSite=Lax, timeout không hoạt động 7 ngày. Dùng `SESSION_COOKIE_SECURE=true` khi triển khai HTTPS.
- Session hiện lưu trong bộ nhớ backend; Redis trong compose chưa được dùng để lưu session.
- `GET /api/auth/csrf` cấp token qua JSON; client lấy token trước mỗi mutation, kể cả đăng ký/đăng nhập/đăng xuất. Không tự động gửi lại mutation khi thất bại.
- Giữ cơ chế CSRF mặc định của Spring Security, bao gồm token được mask và xoay token khi đăng nhập/đăng xuất. Tham khảo [Spring Security CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html).
- Frontend/backend dùng cùng origin qua proxy; chưa cấu hình CORS cho triển khai khác origin.

## Data model
| Bảng | Nội dung / ràng buộc |
|---|---|
| `user_accounts` | id, username unique, password_hash, created_at, updated_at |
| `tasks` | id, owner_id FK, title (1–200 ký tự), scheduled_date DATE, completed, timestamps |

Ngày thực hiện là ngày lịch, không phải timestamp. Frontend lấy ngày hiện tại theo múi giờ thiết bị. Danh sách sắp xếp theo created_at rồi id; index `(owner_id, scheduled_date, created_at, id)`.

Chủ sở hữu lấy từ principal đã xác thực, không nhận từ request body. Mọi thao tác theo id đều truy vấn kèm username của chủ sở hữu; task không tồn tại hoặc thuộc người khác cùng trả 404.

## API
| Method | Endpoint | Body / kết quả |
|---|---|---|
| GET | `/api/auth/csrf` | `{headerName, token}`; public |
| POST | `/api/auth/register` | JSON `{username,password}` → 201; trùng username → 409 |
| POST | `/api/auth/login` | Form `{username,password}` → 204 và session; sai → 401 |
| GET | `/api/auth/me` | `{username}`; chưa đăng nhập → 401 |
| POST | `/api/auth/logout` | 204, vô hiệu hóa session |
| GET | `/api/tasks?date=YYYY-MM-DD` | Task[] của tài khoản hiện tại |
| POST | `/api/tasks` | `{title,scheduledDate}` → 201 |
| PUT | `/api/tasks/{id}` | `{title,scheduledDate}` → 200; giữ nguyên completed |
| PATCH | `/api/tasks/{id}/completion` | `{completed: boolean}` → 200 |
| DELETE | `/api/tasks/{id}` | 204; không tồn tại → 404 |

Task response: `{id,title,scheduledDate,completed}`. API task đều yêu cầu đăng nhập; mutation yêu cầu CSRF. Thay đổi dữ liệu chạy trong transaction.

## Giới hạn hiện tại
- Chưa có rate limiting, khôi phục mật khẩu hoặc email.
- Chưa có optimistic locking: hai tab sửa cùng task thì lần ghi sau thắng.
- Chưa phân trang danh sách công việc theo ngày.
