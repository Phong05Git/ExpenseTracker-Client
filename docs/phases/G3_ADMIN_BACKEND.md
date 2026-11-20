# G3 — Admin Backend và RBAC
Chỉ thực hiện một mã mỗi lượt. Đọc AGENTS.md và PROGRESS.md; kiểm tra gate trước, build/test phù hợp và ghi bằng chứng. Các bước khảo sát/thiết kế không bắt buộc sửa code; không tạo migration/deploy thật nếu chưa duyệt.

## G3.01 — Permission matrix
- Repository: Backend
- Điều kiện: G2.11; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Xác định role, permission, endpoint và dữ liệu tối thiểu; chốt chính sách phiên/quản trị.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Ma trận được duyệt, deny by default.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.02 — RBAC schema
- Repository: Backend
- Điều kiện: G3.01; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Thiết kế entity/config/migration tương thích User hiện tại, review SQL và rollback.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Build, migration test cô lập; duyệt trước apply thật.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.03 — Admin bootstrap
- Repository: Backend
- Điều kiện: G3.02; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Thiết kế khởi tạo admin an toàn không public signup, không mật khẩu mặc định.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Tạo admin có kiểm soát; user không nâng quyền.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.04 — Admin auth
- Repository: Backend
- Điều kiện: G3.03; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Kế thừa auth, thiết kế phiên admin và policy phù hợp.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Đăng nhập admin đúng; user thường bị từ chối; token sai/hết hạn fail.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.05 — Authorization policies
- Repository: Backend
- Điều kiện: G3.04; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Áp permission server-side cho mọi endpoint admin; kiểm tra role thay đổi và token cũ.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test ma trận quyền, 401/403 đúng, không tin client role.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.06 — User listing
- Repository: Backend
- Điều kiện: G3.05; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Danh sách, tìm kiếm, lọc và thông tin cơ bản có pagination.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Không lộ tài chính/token/hash; giới hạn page.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.07 — Account lock
- Repository: Backend
- Điều kiện: G3.06; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Khóa/mở khóa tài khoản và lý do theo policy đã duyệt.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Tài khoản khóa không đăng nhập/refresh trái policy; audit.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.08 — Account activation
- Repository: Backend
- Điều kiện: G3.07; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Vô hiệu hóa/kích hoạt và kiểm tra tác động dữ liệu cũ.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Không xóa dữ liệu ngoài ý muốn; phiên bị xử lý theo policy.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.09 — Role management
- Repository: Backend
- Điều kiện: G3.08; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Cấp/thu hồi role có ràng buộc chống leo quyền và mất admin cuối nếu áp dụng.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: User thường không cấp quyền; token cũ được xử lý.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.10 — Session revocation
- Repository: Backend
- Điều kiện: G3.09; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Thu hồi phiên/refresh token, xác định hiệu lực access token đã phát hành.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test revoke và cửa sổ hiệu lực; không tuyên bố revoke tức thì nếu JWT còn hạn.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.11 — System categories
- Repository: Backend
- Điều kiện: G3.10; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: CRUD phù hợp soft-delete, phân biệt hệ thống/cá nhân và giao dịch cũ.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Không sửa danh mục cá nhân trái quyền; lịch sử giữ nguyên.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.12 — Audit log
- Repository: Backend
- Điều kiện: G3.11; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Ghi actor, action, target, outcome, thời gian cho thao tác nhạy cảm; redaction.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test audit thành công/thất bại, không chứa secret, truy cập audit có quyền.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.13 — Dashboard
- Repository: Backend
- Điều kiện: G3.12; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Tổng hợp tài khoản, giao dịch, danh mục, lỗi và bất thường từ nguồn có thật.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Số liệu kiểm chứng được, không expose chi tiết tài chính; metric thiếu ghi rõ.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G3.14 — Admin security
- Repository: Backend
- Điều kiện: G3.13; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Test auth, IDOR, mass assignment, escalation, stale tokens, rate limit và regression.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Bằng chứng từng case; không còn lỗi nghiêm trọng chưa xử lý.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.
