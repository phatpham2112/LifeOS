# Testing

## Nguyên tắc
- Unit test cho business logic.
- Integration test cho database, security và giao tiếp giữa các thành phần quan trọng.
- Kiểm tra thủ công luồng end-to-end khi cần.

## Checklist khi hoàn thành feature
- [ ] Happy path
- [ ] Input không hợp lệ
- [ ] Dữ liệu không tồn tại
- [ ] Phân quyền / dữ liệu giữa người dùng
- [ ] Lỗi hệ thống, transaction (nếu liên quan)
- [ ] Test chạy thành công

## Test cases đáng chú ý
| Feature | Scenario | Loại | Test class | Kết quả |
|---|---|---|---|---|
| FEAT-001 | Tạo task hợp lệ | Unit | TaskServiceTest | Chưa chạy |
| FEAT-001 | Tiêu đề rỗng | Unit | TaskServiceTest | Chưa chạy |

## Lệnh chạy test
```bash
# Thay bằng lệnh của dự án
./mvnw test
```
