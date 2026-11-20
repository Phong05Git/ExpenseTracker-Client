# G7 — Tổng kiểm thử và nghiệm thu
Chỉ thực hiện một mã mỗi lượt. Đọc AGENTS.md và PROGRESS.md; kiểm tra gate trước, build/test phù hợp và ghi bằng chứng. Các bước khảo sát/thiết kế không bắt buộc sửa code; không tạo migration/deploy thật nếu chưa duyệt.

## G7.01 — Core regression
- Repository: Both
- Điều kiện: G6.10; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Test auth, giao dịch, danh mục, ngân sách, báo cáo và chat cũ.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Kết quả build/runtime từng chức năng.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G7.02 — Admin regression
- Repository: Portal+Backend
- Điều kiện: G7.01; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Test ma trận quyền, phiên, audit, dashboard và quản trị.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Case pass/fail có evidence.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G7.03 — Transport security
- Repository: Both
- Điều kiện: G7.02; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Test HTTPS, hostname, pin, MITM, rotation và Android security cũ.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Bằng chứng trên môi trường đã duyệt.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G7.04 — AI security regression
- Repository: Both
- Điều kiện: G7.03; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Chạy lại bộ test AI, ownership, injection/leakage/abuse.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Ghi model/version, kết quả và residual risks.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G7.05 — Performance
- Repository: Both
- Điều kiện: G7.04; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Đo API, DB, inference và UI theo tải/ngưỡng được duyệt.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Bảng số liệu, cấu hình môi trường và bottleneck.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G7.06 — Recovery
- Repository: Backend
- Điều kiện: G7.05; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Diễn tập restart service/model và restore DB cô lập.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Đối chiếu dữ liệu, downtime và runbook.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G7.07 — Documentation
- Repository: Both
- Điều kiện: G7.06; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Cập nhật kiến trúc, schema, API, threat model, deployment, admin và AI.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Tài liệu khớp source/commit, không secrets.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G7.08 — Acceptance
- Repository: Both
- Điều kiện: G7.07; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Tổng hợp evidence, bugs, deferred scope và quyết định nghiệm thu.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Người dùng duyệt; không đánh dấu hoàn thành nếu thiếu gate.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.
