# LifeOS — Tài liệu tính năng

Nguồn: [LifeOS_Project_Plan.docx](LifeOS_Project_Plan.docx), phiên bản 1.0 ngày 09/10/2026, đặc biệt các mục 1–3, 7, 9 và 14.

Tài liệu chuyển kế hoạch thành phạm vi tính năng và tiêu chí nghiệm thu có thể kiểm chứng. Trạng thái hiện tại được đối chiếu với source và tài liệu trong repository ngày 09/10/2026; đây không phải kết quả kiểm thử mới của toàn bộ hệ thống.

## 1. Mục tiêu và người dùng

LifeOS giúp người dùng lập kế hoạch ngày, duy trì thói quen và nhìn lại tiến bộ trong một nơi. Người dùng đầu tiên là chủ dự án, sử dụng trên laptop và điện thoại; mọi dữ liệu cá nhân vẫn phải được phân quyền để hỗ trợ nhiều tài khoản.

Luồng sử dụng chính:

1. Buổi sáng: mở Today, xem việc hôm nay và quá hạn, chọn 1–3 việc quan trọng.
2. Trong ngày: cập nhật task và check-in habit bằng ít thao tác.
3. Buổi tối: ghi nhật ký ngắn, điều tốt và điều cần cải thiện.
4. Cuối tuần: xem số liệu, viết review và chọn một hành động cải thiện tuần tới.

Mục tiêu sử dụng MVP: mở ứng dụng ít nhất 5 ngày/tuần trong 4 tuần; lập kế hoạch sáng và tổng kết tối dưới 5 phút mỗi lần; thao tác hoàn thành task/check-in trong 1–2 thao tác chính. Dữ liệu đã xác nhận phải được lưu bền vững, có backup đã thử restore. Sau 4 tuần, người dùng có thể nêu ít nhất một thay đổi dựa trên review.

## 2. Phạm vi và trạng thái

`Done`: phạm vi được nêu đã triển khai; `In Progress`: mới hoàn thành một phần; `Backlog`: chưa triển khai; `Blocked`: có trở ngại được ghi rõ. Checkbox đã đánh dấu ghi nhận phạm vi hiện có, không thay thế Definition of Done khi nghiệm thu release.

| ID | Tính năng | Phạm vi | Trạng thái hiện tại | Backlog kế hoạch |
|---|---|---|---|---|
| FEAT-000 | Tài khoản và đăng nhập | MVP | Done — đăng nhập bằng username; còn việc củng cố bảo mật trước triển khai | LIFE-007 |
| FEAT-001 | Quản lý công việc | MVP | In Progress — CRUD theo ngày đã có; thiếu deadline, ưu tiên, lịch sử hoàn thành và hủy | LIFE-004, LIFE-005, LIFE-006 |
| FEAT-002 | Thói quen và check-in | MVP | Backlog | LIFE-008, LIFE-009 |
| FEAT-003 | Nhật ký và review tuần | MVP | Backlog | LIFE-011, LIFE-012 |
| FEAT-004 | Today dashboard | MVP | Backlog — trang hiện tại mới là danh sách task theo ngày | LIFE-010 |
| FEAT-005 | Thiết lập ngày và múi giờ | Hỗ trợ MVP | Backlog | Quy tắc mục 3.3–3.4 |
| FEAT-006 | Trải nghiệm responsive | MVP | In Progress — cần nghiệm thu trên thiết bị thực | LIFE-005, LIFE-010 |
| FEAT-007 | Độ tin cậy và vận hành | Điều kiện phát hành MVP | In Progress — có migration và test; chưa nghiệm thu deploy/backup | LIFE-001–003, LIFE-013, LIFE-014 |
| FEAT-008 | Mục tiêu và milestone | Sau MVP | Backlog | M6 |
| FEAT-009 | Nhắc nhở | Sau MVP, khi có nhu cầu | Backlog | LIFE-016 |
| FEAT-010 | PWA và các mở rộng theo nhu cầu | Sau MVP | Backlog | LIFE-015, mục 2.3 |
| FEAT-011 | Quản lý tài khoản và phân quyền | Mở rộng quản trị theo yêu cầu | Backlog | Bổ sung theo yêu cầu, phụ thuộc LIFE-007 |

Habit, journal và weekly review vẫn thuộc MVP dù backlog gốc gán P1 để triển khai sau luồng task. Thứ tự ưu tiên không đồng nghĩa với loại khỏi MVP.

Không làm trong MVP: microservices, Kubernetes, Kafka, Elasticsearch, ứng dụng native, AI coach, tính năng xã hội/nhóm, gamification phức tạp, offline sync đa thiết bị với xử lý conflict đầy đủ, quản lý tài chính, ghi chú tổng quát và hệ thống plugin.

## 3. FEAT-000 — Tài khoản và đăng nhập

**Mục tiêu:** người dùng truy cập dữ liệu cá nhân an toàn và độc lập với tài khoản khác.

### Phạm vi đã có

- Đăng ký bằng username 3–40 ký tự thường, số hoặc `_`; username duy nhất.
- Mật khẩu tối thiểu 8 ký tự và tối đa 72 byte UTF-8; hash bằng BCrypt.
- Đăng nhập, giữ phiên khi tải lại trang và đăng xuất.
- Session cookie HttpOnly, SameSite=Lax; CSRF cho thao tác ghi.
- Khi phiên hết hạn, giao diện yêu cầu đăng nhập lại.
- Không có tài khoản mặc định; người dùng tự đăng ký.

### Tiêu chí nghiệm thu

- [x] Đăng ký hợp lệ tạo tài khoản; username trùng trả lỗi rõ ràng.
- [x] Input không hợp lệ bị từ chối ở server và hiển thị lỗi trên form.
- [x] Đăng nhập sai trả lỗi; thông tin đúng tạo phiên dùng được ở API riêng tư.
- [x] Tải lại trang giữ phiên còn hiệu lực; đăng xuất vô hiệu hóa phiên.
- [x] Không lưu hoặc trả mật khẩu dạng rõ; request ghi thiếu CSRF bị từ chối.
- [x] Tài khoản B không xem, sửa, hoàn thành hoặc xóa task của A.
- [ ] Các module mới có kiểm thử quyền sở hữu tương tự task.
- [ ] Trước triển khai: nghiệm thu HTTPS, cookie Secure và cấu hình secret.

**Giới hạn:** session lưu trong tiến trình backend; restart backend cần đăng nhập lại. Chưa có khôi phục mật khẩu, xác minh email, OAuth hoặc giới hạn số lần đăng nhập. Kế hoạch gốc gợi ý email, nhưng source hiện dùng username; chưa đổi cơ chế đăng nhập trong phạm vi tài liệu này.

**Tham chiếu:** module `auth`, API `/api/auth/*`, `AccountTaskIntegrationTest`; [DESIGN.md](DESIGN.md).

## 4. FEAT-001 — Quản lý công việc hằng ngày

**User story:** tôi muốn ghi việc cần làm, chọn ngày và mức ưu tiên, cập nhật kết quả để biết hôm nay cần tập trung vào đâu.

### Phạm vi

- Tạo, xem và sửa task; tiêu đề bắt buộc, mô tả tùy chọn.
- Chọn ngày thực hiện, deadline theo ngày và độ ưu tiên.
- Hoàn thành, mở lại và hủy task; lưu thời điểm hoàn thành.
- Xem việc theo ngày và nhận diện việc quá hạn.
- Bảo toàn dữ liệu cần cho thống kê lịch sử khi hủy/xóa.

### Quy tắc

- Task chỉ thuộc một tài khoản; chủ sở hữu lấy từ phiên đăng nhập.
- Tiêu đề sau trim không rỗng, tối đa 200 ký tự; ngày phải hợp lệ.
- Kế hoạch cho phép bắt đầu với TODO/DONE. Source hiện lưu `completed` boolean; bổ sung CANCELLED khi triển khai hủy, chỉ thêm IN_PROGRESS nếu cần.
- Chuyển sang DONE phải lưu `completed_at`; mở lại phải xử lý thời điểm này nhất quán.
- Deadline MVP là ngày lịch. `scheduledDate` hiện có là ngày thực hiện, chưa phải deadline; không tự coi hai trường có cùng ý nghĩa.
- Thứ tự mục tiêu: nhóm quá hạn trước, sau đó ưu tiên và deadline; có thứ tự phụ ổn định khi bằng nhau.
- Task DONE/CANCELLED không xuất hiện trong nhóm quá hạn.
- Source hiện xóa vật lý. Trước khi triển khai review phải chốt hủy/soft delete và chính sách thống kê, tránh mất lịch sử ngoài ý muốn.

### Tiêu chí nghiệm thu

- [x] Tạo và xem task theo ngày trên giao diện.
- [x] Tiêu đề rỗng/quá dài và ngày không hợp lệ bị từ chối.
- [x] Sửa tiêu đề, đổi ngày; giữ trạng thái hoàn thành khi sửa.
- [x] Hoàn thành/bỏ hoàn thành được lưu; request lặp cùng trạng thái không làm đảo trạng thái.
- [x] Xóa có xác nhận; hủy xác nhận không xóa dữ liệu.
- [x] API kiểm tra quyền sở hữu; id không tồn tại/thuộc người khác trả 404.
- [x] Giao diện có tải/rỗng/lỗi, giữ nội dung form khi lưu thất bại.
- [ ] Có deadline, ưu tiên và hiển thị quá hạn đúng theo ngày của user.
- [ ] Hoàn thành lưu `completed_at`; mở lại và hoàn thành lặp có quy tắc được kiểm thử.
- [ ] Hủy task và xử lý lịch sử đáp ứng quy tắc review đã chốt.
- [ ] Dữ liệu vẫn đúng sau restart; luồng tạo → hoàn thành → review được kiểm tra xuyên suốt.

**Tham chiếu:** module `task`, API hiện tại `/api/tasks`, `TaskServiceTest`, `AccountTaskIntegrationTest`, `HomeView.spec.ts`.

## 5. FEAT-002 — Thói quen và check-in

**User story:** tôi muốn đặt lịch đọc sách, học hoặc vận động và ghi nhận kết quả từng ngày để đánh giá mức độ duy trì.

### Phạm vi

- Tạo, xem, sửa và ngừng theo dõi habit.
- Tên, mô tả tùy chọn và lịch lặp theo các ngày trong tuần.
- Danh sách habit đến lịch trong ngày; ghi nhận hoàn thành hoặc chủ động bỏ qua.
- Xem/cập nhật kết quả ngày đã qua và thống kê cơ bản.

### Quy tắc

- Habit là định nghĩa; habit log là kết quả theo ngày, không gộp hai loại dữ liệu.
- Tối đa một log cho `(user, habit, local_date)`, được bảo vệ bằng unique constraint.
- Ngày check-in dựa trên timezone user; không dùng ngày UTC hay ngày server thay thế.
- `completed`: đã thực hiện; `skipped`: chủ động bỏ qua; `missed`: ngày đến lịch đã qua nhưng chưa hoàn thành/bỏ qua. Ngày hiện tại chưa ghi nhận là đang chờ, ngày tương lai chưa có kết quả; không gán cả hai thành missed.
- Sửa lịch không tự động viết lại lịch sử. Cần lưu đủ dữ liệu lịch có hiệu lực để tính số ngày dự kiến trong quá khứ.
- Không tính ngày tương lai hoặc ngày không đến lịch là bỏ lỡ.
- Streak nếu bổ sung chỉ là thông tin hỗ trợ, không dùng để kết luận người dùng thất bại.

### Tiêu chí nghiệm thu

- [ ] Tạo habit với lịch hợp lệ; hiển thị đúng các ngày được chọn.
- [ ] Giao diện Today hiển thị habit cần check-in trong ngày của user.
- [ ] Check-in hoàn thành trong 1–2 thao tác chính và hiện kết quả đã lưu.
- [ ] Hai request đồng thời/cùng ngày không tạo hai log; cập nhật lặp có kết quả nhất quán.
- [ ] Check-in hôm qua và hôm nay được lưu đúng ngày, có test biên nửa đêm.
- [ ] Phân biệt completed, skipped, missed, đang chờ và ngày không đến lịch trên giao diện.
- [ ] Đổi lịch, ngừng theo dõi hoặc đổi timezone không tự sửa ngày/kết quả lịch sử.
- [ ] Không đọc/ghi habit hoặc log của tài khoản khác.
- [ ] Có test unique constraint, lịch tuần, timezone và công thức thống kê.

**Phụ thuộc:** FEAT-000, FEAT-005. Module dự kiến: `habit`; API trong kế hoạch là phác thảo, chưa triển khai.

## 6. FEAT-003 — Nhật ký và review tuần

Giữ ID FEAT-003 hiện có; tách hai phần để nghiệm thu độc lập.

### 6.1. Daily journal

**User story:** tôi muốn ghi vài dòng cuối ngày về điều tốt và điều cần cải thiện mà không phải điền nhiều trường.

**Phạm vi:** xem, tạo và cập nhật journal theo ngày; highlights, improvements hoặc ghi chú; mood và energy tùy chọn.

**Quy tắc:** tối đa một journal chính cho mỗi `(user, local_date)`; được cập nhật nhiều lần. Không ghi nhật ký không bị xem là lỗi hoặc thất bại. Không log nội dung nhật ký. Thang mood/energy và giới hạn nội dung phải được chốt trước triển khai.

**Tiêu chí nghiệm thu:**

- [ ] Lưu và mở lại journal đúng ngày, đúng user.
- [ ] Lưu nhiều lần trong cùng ngày cập nhật một journal, không tạo bản trùng.
- [ ] Có thể lưu khi không nhập mood/energy; nội dung trống được xử lý theo quy tắc đã chốt.
- [ ] Đổi ngày xem không làm mất bản nháp mà không thông báo.
- [ ] Lưu thất bại giữ nội dung nhập và cho phép thử lại.
- [ ] Unique constraint và quyền sở hữu được kiểm thử trên database.

### 6.2. Weekly review

**User story:** tôi muốn xem kết quả tuần, ghi nhận điều đã học và chọn một hành động cải thiện cho tuần tới.

**Phạm vi:** chọn tuần; xem số task hoàn thành, tỷ lệ check-in habit, journal liên quan; lưu ghi chú tổng kết và hành động tuần tới.

**Quy tắc trong kế hoạch:** khoảng tuần phải nhất quán với timezone và ngày bắt đầu tuần của user; số liệu được tính từ dữ liệu gốc, chỉ thêm cache khi có lý do hiệu năng. Ngày nghỉ/không có kế hoạch không bị coi là lỗi.

**Đề xuất công thức để chốt trước triển khai** (kế hoạch gốc chưa quy định đầy đủ):

- Task hoàn thành: số task hiện ở DONE có `completed_at` thuộc tuần theo timezone user; task mở lại không còn được tính. Nếu cần giữ mọi lần hoàn thành trong lịch sử phải bổ sung sự kiện trạng thái, không suy từ boolean hiện tại.
- Habit completion rate = số lần completed / số lần đến lịch đã có thể đánh giá × 100%.
- Mẫu số gồm ngày đến lịch đã qua; ngày hiện tại chỉ đưa vào khi có completed/skipped. Skipped vẫn thuộc mẫu số nhưng không thuộc tử số; ngày tương lai và ngày không đến lịch bị loại.
- Với tuần đang diễn ra, hiển thị đây là số liệu tạm tính. Mẫu số 0 hiển thị “Chưa có dữ liệu”, không hiển thị 0% hoặc 100%.
- Thống kê dùng lịch có hiệu lực ở ngày được thống kê. Chính sách hủy/xóa task và ngừng theo dõi habit phải giữ được dữ liệu cần tính.

**Tiêu chí nghiệm thu:**

- [ ] Chọn tuần hiển thị rõ ngày bắt đầu/kết thúc theo thiết lập user.
- [ ] Task và habit thống kê khớp dữ liệu mẫu; giao diện giải thích được công thức.
- [ ] Không tính ngày chưa đến là missed; skipped và thiếu dữ liệu được phân biệt.
- [ ] Có test tuần qua tháng/năm, timezone, tuần chưa kết thúc và mẫu số 0.
- [ ] Lưu/mở lại ghi chú tổng kết và hành động cải thiện, không tạo bản trùng cho cùng tuần/user.
- [ ] Review và journal chỉ truy cập được bởi chủ sở hữu.

**Phụ thuộc:** journal cần FEAT-000/005; review cần FEAT-001/002/005 và các quy tắc lịch sử đã chốt. Module dự kiến: `journal`, `review`.

## 7. FEAT-004 — Today dashboard

**User story:** tôi muốn mở một màn hình để biết việc cần làm và habit cần thực hiện hôm nay.

### Phạm vi

- Hiển thị ngày hiện tại theo timezone user.
- Các nhóm task hôm nay, task quá hạn và habit đến lịch.
- Tạo task, hoàn thành/mở lại task và check-in ngay trên màn hình.
- Hiển thị tiến độ đơn giản và lối vào journal cuối ngày.
- Hỗ trợ người dùng chọn việc quan trọng bằng ưu tiên; chưa thêm cơ chế xếp hạng phức tạp.

### Tiêu chí nghiệm thu

- [ ] Task và habit của cùng user xuất hiện đúng nhóm/ngày.
- [ ] Task DONE/CANCELLED không bị hiển thị là quá hạn.
- [ ] Sau cập nhật, tiến độ và trạng thái đổi theo dữ liệu server đã xác nhận.
- [ ] Có trạng thái tải, chưa có việc/thói quen, lỗi và thử lại.
- [ ] Một phần tải lỗi không làm mất dữ liệu/phần đã tải thành công.
- [ ] Số liệu không trộn giữa tài khoản hoặc giữa ngày cũ và ngày mới.
- [ ] Luồng buổi sáng và check-in dùng được trên laptop/điện thoại.

**Phụ thuộc:** FEAT-001, FEAT-002, FEAT-005; liên kết journal khi FEAT-003 có thể sử dụng.

## 8. FEAT-005 — Thiết lập ngày và múi giờ

**Mục tiêu:** “hôm nay”, check-in và tuần review có cùng ý nghĩa ở mọi thiết bị.

**Phạm vi mục tiêu:** lưu timezone IANA và ngày bắt đầu tuần của user; dùng thiết lập này cho Today, habit, journal và review. Source hiện lấy ngày task từ múi giờ thiết bị, chưa có thiết lập lưu ở user.

### Tiêu chí nghiệm thu

- [ ] User có thể xem/cập nhật timezone và ngày bắt đầu tuần; server từ chối giá trị không hợp lệ.
- [ ] Thiết lập ban đầu được xác định rõ, không phụ thuộc ngầm vào timezone server.
- [ ] Đổi timezone ảnh hưởng cách xác định ngày hiện tại, không tự dời local_date lịch sử.
- [ ] Timestamp hoàn thành task được quy đổi đúng khi tổng hợp theo tuần.
- [ ] Có test quanh nửa đêm và thay đổi giờ mùa hè với timezone hỗ trợ DST.

## 9. FEAT-006 — Trải nghiệm responsive và thao tác nhanh

**Mục tiêu:** dùng thường xuyên trên điện thoại và laptop với ít ma sát.

### Tiêu chí nghiệm thu

- [ ] Luồng đăng nhập, task, habit, Today, journal và review không bị che/cắt trên thiết bị mục tiêu.
- [ ] Form có label, lỗi dễ hiểu; thao tác chính dùng được bằng bàn phím và có focus rõ ràng.
- [ ] Không báo thành công khi server chưa xác nhận; lỗi lưu giữ nội dung người dùng đã nhập.
- [ ] Hành động gây mất dữ liệu có xác nhận phù hợp.
- [ ] Thao tác hoàn thành/check-in đạt mục tiêu 1–2 thao tác chính.
- [ ] Dùng thử thực tế ít nhất 3 ngày cho luồng task, ghi nhận các thao tác gây bất tiện.

PWA, offline và phím tắt nâng cao không phải điều kiện hoàn thành phần responsive của MVP.

## 10. FEAT-007 — Độ tin cậy, dữ liệu và vận hành

**Mục tiêu:** dữ liệu đã lưu không mất sau restart và có thể phục hồi khi xảy ra sự cố.

### Tiêu chí nghiệm thu

- [x] PostgreSQL chạy local bằng Compose; backend dùng Flyway và Hibernate validate schema.
- [x] Có unit/API/integration test cho auth và task; có frontend test cho tương tác chính.
- [x] README hướng dẫn setup, chạy ứng dụng và test.
- [ ] CI tự chạy build/test và chặn thay đổi lỗi trước release.
- [ ] Có môi trường triển khai sử dụng HTTPS, secret ngoài Git và health check.
- [ ] Cấu hình backup tự động, quy định tần suất/lưu giữ theo nhu cầu thực tế.
- [ ] Thử restore vào database riêng và đối chiếu dữ liệu; có hướng dẫn lặp lại được.
- [ ] Kiểm tra dữ liệu vẫn còn sau restart backend/database.
- [ ] Log đủ để tìm lỗi nhưng không chứa password, token hoặc nội dung journal.
- [ ] Có quy trình export phù hợp hoặc backup phục vụ nhu cầu cá nhân; export/xóa dữ liệu theo user phải có trước khi mở rộng nhiều người dùng.

**Lưu ý:** checkbox có test ghi nhận bộ test hiện có; kết quả lần chạy và giới hạn nằm ở [TESTING.md](TESTING.md). Sự hiện diện của test không đồng nghĩa đã nghiệm thu vận hành.

## 11. Tính năng sau MVP

### FEAT-008 — Goals và milestones

Chỉ triển khai khi người dùng cần nối mục tiêu dài hạn với hành động hằng ngày.

- Goal có tiêu đề, mô tả, ngày bắt đầu, deadline, trạng thái và tiêu chí thành công.
- Task có thể liên kết goal nhưng không bắt buộc.
- Tiến độ phải dựa trên milestone hoặc chỉ số có định nghĩa; không tự suy ra phần trăm.
- Nghiệm thu: tạo/sửa goal, liên kết task, cập nhật milestone và kiểm tra quyền sở hữu; công thức tiến độ được giải thích nếu có.

### FEAT-009 — Nhắc nhở

Chỉ triển khai khi việc quên task/check-in xảy ra trong sử dụng thực tế.

- Kênh email/push được chọn theo nhu cầu; user chủ động cấu hình và tắt nhắc nhở.
- Lịch gửi theo timezone; trạng thái gửi và lỗi có thể quan sát.
- Nghiệm thu: gửi đúng lịch, hạn chế gửi trùng theo quy tắc đã chốt; lỗi gửi không làm hỏng thao tác lưu task/habit; có test retry.

### FEAT-010 — Mở rộng theo nhu cầu

| Nhu cầu | Phạm vi cân nhắc | Điều kiện trước triển khai |
|---|---|---|
| Nhập liệu còn chậm | Quick capture, phím tắt | Có thao tác gây ma sát được ghi nhận |
| Tìm ghi chú cũ | Search nâng cao | Nhu cầu tìm kiếm vượt danh sách/lọc hiện tại |
| Mạng yếu, muốn cài app | PWA cache, offline có kiểm soát | Chốt dữ liệu offline, trạng thái đồng bộ và xử lý conflict |
| Theo dõi thời gian | Time tracking đơn giản | Chốt ý nghĩa phiên đo và cách tổng hợp |
| Nhìn xu hướng dài hạn | Phân tích tháng | Công thức tuần và dữ liệu lịch sử đã ổn định |

Các tính năng này đều ở Backlog, chưa phải cam kết phát hành. Cache/broker hoặc kiến trúc mới cần vấn đề cụ thể và bằng chứng đo lường.

### FEAT-011 — Quản lý tài khoản và phân quyền

**Trạng thái:** Backlog. Bổ sung theo yêu cầu người dùng; kế hoạch gốc chưa mô tả màn hình quản trị. Source hiện chỉ cấp vai trò `USER`, chưa có chức năng quản trị.

**User story:** với vai trò quản trị viên, tôi muốn xem danh sách tài khoản, khóa/mở khóa và quản lý vai trò để kiểm soát người có thể sử dụng hệ thống.

#### Phạm vi

- Màn hình quản trị tài khoản dành cho `ADMIN`, có danh sách phân trang, tìm kiếm theo username và lọc theo trạng thái/vai trò.
- Xem thông tin quản trị: id, username, trạng thái, vai trò và ngày tạo; không hiển thị password hash hoặc secret.
- Khóa/mở khóa tài khoản, có xác nhận và lý do khóa.
- Gán/thu hồi vai trò `ADMIN`; mọi tài khoản giữ quyền `USER` để sử dụng chức năng cá nhân.
- Ghi lịch sử thao tác quản trị: người thực hiện, tài khoản bị tác động, hành động, thời điểm, lý do và thay đổi trước/sau.
- Có quy trình cấp quyền cho admin đầu tiên qua công cụ hoặc cấu hình vận hành được kiểm soát.

Không bao gồm trong phiên bản đầu: vai trò tùy chỉnh, phân quyền đến từng nút/màn hình, nhóm/tổ chức, admin đặt mật khẩu thay người dùng hoặc đọc dữ liệu cá nhân của người khác.

#### Ma trận quyền

| Hành động | USER | ADMIN |
|---|---|---|
| Quản lý task, habit, journal và review của chính mình | Có | Có |
| Đọc/sửa dữ liệu cá nhân của tài khoản khác | Không | Không |
| Xem danh sách và thông tin quản trị tài khoản | Không | Có |
| Khóa/mở khóa tài khoản khác | Không | Có, theo quy tắc bảo vệ admin |
| Gán/thu hồi vai trò ADMIN | Không | Có, theo quy tắc bảo vệ admin |
| Xem lịch sử thao tác quản trị | Không | Có |

#### Quy tắc nghiệp vụ

- Tài khoản đăng ký mới chỉ có `USER`; client không được tự gửi role để nâng quyền.
- Kiểm tra quyền ở backend cho mọi API quản trị. Ẩn menu trên frontend chỉ phục vụ trải nghiệm, không thay thế authorization.
- Quyền quản trị tài khoản không cho phép bỏ qua kiểm tra chủ sở hữu của task, habit, journal hoặc review.
- Tài khoản bị khóa không được đăng nhập hoặc tiếp tục dùng phiên cũ; mở khóa không khôi phục phiên đã vô hiệu hóa.
- Thay đổi vai trò có hiệu lực với mọi phiên đang tồn tại: thu hồi ADMIN phải chặn ngay request quản trị tiếp theo, không chờ user đăng xuất.
- Admin không tự khóa tài khoản hoặc tự thu hồi quyền ADMIN trong phiên bản đầu.
- Luôn giữ ít nhất một admin không bị khóa. Quy tắc phải đúng cả khi nhiều request đồng thời khóa/thu hồi quyền các admin khác nhau.
- Cấp admin đầu tiên phải là thao tác có chủ đích, có thể kiểm tra và lặp lại an toàn; không tự phong tài khoản đăng ký đầu tiên và không tạo mật khẩu mặc định cố định.
- Thay đổi trạng thái/vai trò và bản ghi audit phải cùng thành công hoặc cùng thất bại. Audit chỉ đọc qua giao diện, không chứa mật khẩu, token hoặc nội dung dữ liệu cá nhân.
- Khóa tài khoản giữ nguyên dữ liệu cá nhân; xóa tài khoản và chính sách lưu giữ dữ liệu nằm ngoài phạm vi phiên bản đầu.

#### Tiêu chí nghiệm thu

- [ ] ADMIN truy cập được màn hình quản trị; USER bị từ chối kể cả gọi API trực tiếp.
- [ ] API quản trị trả 401 khi chưa đăng nhập, 403 khi thiếu quyền; thao tác ghi được bảo vệ CSRF.
- [ ] Danh sách phân trang, tìm kiếm và lọc đúng; giao diện có tải/rỗng/lỗi và giữ bộ lọc sau cập nhật.
- [ ] Đăng ký với payload chứa vai trò không thể tạo tài khoản ADMIN.
- [ ] Khóa có xác nhận/lý do; tài khoản bị khóa không đăng nhập được và mọi phiên cũ bị chặn ở request tiếp theo.
- [ ] Mở khóa cho phép đăng nhập lại bằng thông tin hiện có, không phục hồi phiên cũ.
- [ ] Gán ADMIN mở quyền quản trị; thu hồi ADMIN chặn quyền ở mọi phiên hiện có nhưng vẫn giữ quyền USER.
- [ ] Từ chối tự khóa/tự thu hồi ADMIN và thao tác khiến không còn admin hoạt động, kể cả request đồng thời.
- [ ] Mỗi thay đổi có audit đầy đủ; lỗi lưu audit không để lại thay đổi tài khoản một phần.
- [ ] Admin không đọc/sửa task, habit, journal hoặc review của người khác; có test truy cập chéo user.
- [ ] Có migration cho vai trò, trạng thái tài khoản và audit; dữ liệu tài khoản hiện có được giữ lại, mặc định USER và không bị khóa.
- [ ] Quy trình cấp admin đầu tiên được ghi trong tài liệu vận hành và có kiểm tra; không nâng quyền ngoài ý muốn khi chạy lại.
- [ ] Có integration test quyền truy cập, session cũ, bảo vệ admin cuối cùng và transaction; có test UI cho thao tác quản trị quan trọng.

**Phụ thuộc:** FEAT-000; phối hợp FEAT-007 để cấp admin đầu tiên và ghi hướng dẫn vận hành. Module dự kiến: mở rộng `auth` cho role/trạng thái và thêm `admin` cho use case quản trị. Endpoint/schema cụ thể cần chốt khi triển khai.

## 12. Thứ tự triển khai và điều kiện hoàn thành

1. Hoàn thiện task: deadline/ưu tiên, `completed_at`, hủy và chính sách lịch sử (M2).
2. Chốt timezone/lịch, triển khai habit và chống check-in trùng (M3).
3. Today, journal và nghiệm thu responsive (M4).
4. Weekly review cơ bản; chốt công thức và kiểm thử dữ liệu nguồn. Review thuộc MVP dù roadmap dành thêm M6 để mở rộng review/goals.
5. Nghiệm thu bảo mật, CI, deploy và backup/restore trước phát hành MVP (M5).
6. Dùng thật vài tuần, ghi ma sát và đánh giá chỉ số trước khi mở rộng.

Hạ tầng bảo mật/CI/backup được bổ sung xuyên suốt; danh sách trên không yêu cầu trì hoãn chúng tới cuối.

FEAT-011 là nhánh mở rộng quản trị sau FEAT-000, có thể triển khai khi cần quản lý nhiều tài khoản; không làm thay đổi phạm vi MVP của kế hoạch gốc.

### Definition of Done dùng cho mọi tính năng

- [ ] Acceptance criteria được kiểm tra, có validation và xử lý lỗi thường gặp.
- [ ] Server kiểm tra quyền sở hữu ở mọi thao tác dữ liệu cá nhân.
- [ ] Có test phù hợp cho quy tắc nghiệp vụ, database/security và tương tác UI quan trọng.
- [ ] Có migration version khi schema thay đổi.
- [ ] UI có tải/rỗng/thành công/lỗi khi phù hợp; dùng thử trên thiết bị mục tiêu.
- [ ] Log không lộ secret hoặc dữ liệu riêng tư không cần thiết.
- [ ] Tài liệu setup/API được cập nhật; pipeline build/test thành công.

### Các quyết định cần chốt khi bắt đầu tính năng liên quan

| Quyết định | Thời điểm cần chốt |
|---|---|
| Quan hệ ngày thực hiện và deadline; mức ưu tiên; trạng thái task | Trước mở rộng FEAT-001 |
| Mở lại task, `completed_at`, hủy/soft delete và tác động thống kê | Trước FEAT-001 mở rộng và review |
| Timezone/ngày bắt đầu tuần mặc định; thay lịch habit có hiệu lực từ ngày nào | Trước FEAT-002/005 |
| Check-in ngày tương lai/ngoài lịch; cửa sổ sửa quá khứ; ngừng theo dõi habit | Trước FEAT-002 |
| Thang mood/energy, giới hạn nội dung và ý nghĩa journal trống | Trước journal |
| Công thức review, skipped, tuần đang diễn ra và cách lưu ghi chú tuần | Trước weekly review |
| Môi trường deploy, tần suất backup, lưu giữ và mục tiêu phục hồi | Trước phát hành MVP |
| Cấp admin đầu tiên, cách áp dụng role/trạng thái cho phiên cũ, bảo vệ admin cuối cùng và lưu giữ audit | Trước FEAT-011 |

## 13. Tài liệu liên quan

- [Kế hoạch gốc](LifeOS_Project_Plan.docx): tầm nhìn, roadmap và backlog LIFE-xxx.
- [Thiết kế hiện tại](DESIGN.md): schema, API thực tế và giới hạn triển khai.
- [Kiểm thử](TESTING.md): phạm vi, lệnh chạy và kết quả được ghi nhận.
- [Mẫu tính năng](templates/FEATURE_TEMPLATE.md): dùng khi tách feature thành công việc nhỏ hơn.
- [README](../README.md): setup và sử dụng local.

API `/api/v1/*` trong kế hoạch là đề xuất; source hiện dùng `/api/auth/*` và `/api/tasks`. Tài liệu này không đổi API hoặc database, và không coi các endpoint dự kiến là đã tồn tại.
