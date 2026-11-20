# G5 — Local AI Infrastructure
Chỉ thực hiện một mã mỗi lượt. Đọc AGENTS.md và PROGRESS.md; kiểm tra gate trước, build/test phù hợp và ghi bằng chứng. Các bước khảo sát/thiết kế không bắt buộc sửa code; không tạo migration/deploy thật nếu chưa duyệt.

## G5.01 — Hardware/runtime
- Repository: Backend
- Điều kiện: G4.09; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Đo tài nguyên host, so sánh runtime local, model và license.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Báo cáo RAM/VRAM và lựa chọn ứng viên; duyệt trước tải lớn.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G5.02 — Model benchmark
- Repository: Backend
- Điều kiện: G5.01; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Đo tiếng Việt, độ chính xác, latency, RAM/VRAM với dữ liệu giả.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Bảng benchmark tái lập, người dùng duyệt model.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G5.03 — Local runtime
- Repository: Backend
- Điều kiện: G5.02; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Chạy model nội bộ, giới hạn bind/access và cấu hình.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Health/inference test; không DB credentials hoặc cloud fallback.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G5.04 — Provider abstraction
- Repository: Backend
- Điều kiện: G5.03; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Tạo interface/provider theo kiến trúc hiện có, cấu hình model, mock.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Unit test success/error/cancel, không khóa cứng provider.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G5.05 — Orchestration
- Repository: Backend
- Điều kiện: G5.04; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Quản lý timeout, cancellation, concurrency, queue/budget và lỗi runtime.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Load test có giới hạn; core API không hỏng khi model down.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G5.06 — Financial aggregation
- Repository: Backend
- Điều kiện: G5.05; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Tính số liệu từ dữ liệu user đã xác thực, khoảng thời gian có giới hạn, xử lý thiếu dữ liệu.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Đối chiếu số liệu với DB test, cross-user fail.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G5.07 — Context builder
- Repository: Backend
- Điều kiện: G5.06; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Chỉ đưa aggregate cần thiết, giảm PII, giới hạn lịch sử/token, đánh dấu dữ liệu không tin cậy.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test context không chứa user khác/secret, không cấp quyền DB cho AI.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G5.08 — AI infrastructure gate
- Repository: Backend
- Điều kiện: G5.07; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Test pipeline mock và local, latency, timeout, restart.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Có evidence runtime/model; không chỉ build.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.
