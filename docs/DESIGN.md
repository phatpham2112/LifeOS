# Technical Design

Chỉ ghi thiết kế có giá trị tham khảo lâu dài, không chép lại code.

## Kiến trúc tổng quan
- Kiểu kiến trúc: monolith Spring Boot 4 (Java 21), package-by-feature trong `com.phatpham.lifeos`.
- Các module: `common` (exception, persistence dùng chung); mỗi feature một package riêng, ví dụ `task` (controller → service → repository → entity).
- Cách module giao tiếp: gọi service trực tiếp, không truy cập repository của module khác.
- Database: PostgreSQL 17, schema quản lý bằng Flyway (`backend/src/main/resources/db/migration`), Hibernate chỉ `validate`.
- Lỗi API: trả về `ProblemDetail` (RFC 9457) qua `GlobalExceptionHandler`.
- Frontend: Vue 3 + TypeScript + Vite, Vue Router, Pinia. Gọi API qua `src/api/http.ts` (ném `ApiError` chứa ProblemDetail); dev dùng Vite proxy `/api` → `:8080` nên không cần CORS.

## Data model
| Entity | Mục đích | Quan hệ / ràng buộc |
|---|---|---|
| Task | Công việc cá nhân | [Bổ sung khi thiết kế] |

## Luồng xử lý quan trọng
### [Tên luồng]
1. [Bước 1]
2. [Bước 2]
3. [Lỗi và cách xử lý]

## API quan trọng
| Method | Endpoint | Mục đích | Ghi chú |
|---|---|---|---|
| POST | `/api/tasks` | Tạo công việc | Validation, authorization |

## Quyết định kỹ thuật
### [Ngày] — [Quyết định]
- **Vấn đề:**
- **Các phương án:**
- **Lựa chọn:**
- **Lý do / trade-off:**
