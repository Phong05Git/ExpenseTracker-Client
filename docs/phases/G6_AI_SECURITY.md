# G6 — AI Chatbot và bảo mật AI
Chỉ thực hiện một mã mỗi lượt. Đọc AGENTS.md và PROGRESS.md; kiểm tra gate trước, build/test phù hợp và ghi bằng chứng. Các bước khảo sát/thiết kế không bắt buộc sửa code; không tạo migration/deploy thật nếu chưa duyệt.

## G6.01 — AI API contract
- Repository: Both
- Điều kiện: G5.08; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Thiết kế sinh phản hồi, lỗi/retry/idempotency và tương thích bốn endpoint cũ.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Duyệt contract giữa repo trước code.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G6.02 — Conversation context
- Repository: Backend
- Điều kiện: G6.01; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Lấy lịch sử đúng owner, giới hạn turn/token, phân tách chỉ dẫn và dữ liệu.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test cross-user, lịch sử dài, dữ liệu giả độc hại.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G6.03 — Assistant persistence
- Repository: Backend
- Điều kiện: G6.02; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Lưu assistant server-side, xử lý lỗi giữa user/assistant, retry không nhân bản ngoài thiết kế.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test lưu/đọc, lỗi model và retry; client không đặt role.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G6.04 — Financial advice
- Repository: Backend
- Điều kiện: G6.03; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Tư vấn thói quen, ngân sách, xu hướng từ aggregate; nêu thiếu dữ liệu và không bịa con số.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Bộ câu hỏi tiếng Việt có expected facts; không tự giao dịch.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G6.05 — Android AI integration
- Repository: Android
- Điều kiện: G6.04; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Nối hợp đồng đã duyệt, trạng thái generating/error/retry, hiển thị assistant an toàn.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test chuyển phiên, logout, retry, offline và lifecycle.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G6.06 — Prompt boundaries
- Repository: Backend
- Điều kiện: G6.05; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Tách system instructions, user input, financial context; coi mọi output không tin cậy.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Không coi prompt là authorization; test direct/indirect injection.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G6.07 — Input/output controls
- Repository: Both
- Điều kiện: G6.06; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Giới hạn input/output, kiểm tra cấu trúc nếu có, render an toàn, không log dữ liệu nhạy cảm.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test payload dài, malformed, nội dung nguy hiểm và lỗi inference.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G6.08 — Data leakage
- Repository: Backend
- Điều kiện: G6.07; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Test prompt extraction, PII, secrets, cross-user và context tampering với fixture giả.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Không lộ dữ liệu trái quyền; ghi rõ giới hạn phòng thủ.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G6.09 — Resource abuse
- Repository: Backend
- Điều kiện: G6.08; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Test rate/concurrency/token limit, timeout, cancellation dưới tải có kiểm soát.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Đo latency/resource; request thường không bị starvation ngoài ngưỡng duyệt.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G6.10 — AI quality & security gate
- Repository: Both
- Điều kiện: G6.09; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Đánh giá hallucination, injection trực tiếp/gián tiếp, thiếu dữ liệu, regression.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Báo cáo pass/fail và residual risks; không tuyên bố chống tuyệt đối.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.
