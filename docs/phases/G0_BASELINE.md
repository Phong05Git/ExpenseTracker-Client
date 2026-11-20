# G0 — Xác minh baseline
Chỉ thực hiện một mã mỗi lượt. Đọc AGENTS.md và PROGRESS.md; kiểm tra gate trước, build/test phù hợp và ghi bằng chứng. Các bước khảo sát/thiết kế không bắt buộc sửa code; không tạo migration/deploy thật nếu chưa duyệt.

## G0.01 — Git baseline
- Repository: Both
- Điều kiện: HEAD và môi trường được xác định; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Đối chiếu HEAD, git status, source, tài liệu và commit đã push; lập danh sách sai khác.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Ghi SHA từng repo và sai khác, không ghi đè thay đổi.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G0.02 — Dependency
- Repository: Both
- Điều kiện: G0.01; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Kiểm tra SDK, Gradle, csproj, lock/version và cảnh báo hiện hành.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Ghi phiên bản thực tế, build không nâng cấp hàng loạt.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G0.03 — Database baseline
- Repository: Backend
- Điều kiện: G0.02; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Đối chiếu migration snapshot và lịch sử DB bằng truy vấn chỉ đọc đã được phép.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Xác định migration thiếu và nguy cơ startup migrate; không apply.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G0.04 — Regression baseline
- Repository: Both
- Điều kiện: G0.03; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Chạy build và test hiện có trên môi trường cô lập.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Lưu lệnh, exit code, test fail và blocker.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.
