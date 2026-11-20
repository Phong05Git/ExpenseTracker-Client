# G1 — Hoàn thiện chat persistence
Chỉ thực hiện một mã mỗi lượt. Đọc AGENTS.md và PROGRESS.md; kiểm tra gate trước, build/test phù hợp và ghi bằng chứng. Các bước khảo sát/thiết kế không bắt buộc sửa code; không tạo migration/deploy thật nếu chưa duyệt.

## UI-5.1.6.3 — Repository & Mapper
- Repository: Android
- Điều kiện: G0.04; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Khảo sát pattern repository hiện hữu; tạo contract, mapper và implementation, giữ envelope và pagination.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test mapping null/empty/error và thứ tự, build.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## UI-5.1.6.4 — Use Cases & DI
- Repository: Android
- Điều kiện: UI-5.1.6.3; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Tạo use case cho bốn thao tác chat và binding Hilt theo convention.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Build graph Hilt; test input và chuyển lỗi.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## UI-5.1.6.5 — ViewModel
- Repository: Android
- Điều kiện: UI-5.1.6.4; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Quản lý phiên, tin nhắn, loading/error, pagination, cancel và race khi đổi phiên/logout.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test state, không rò dữ liệu tài khoản cũ, không gửi trùng.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## UI-5.1.6.6 — UI Integration
- Repository: Android
- Điều kiện: UI-5.1.6.5; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Kết nối fragment/layout hiện có, tạo/mở phiên, gửi/đọc lịch sử, empty/error.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Thiết bị thực hiện đủ luồng, không giả lập assistant.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## UI-5.1.6.7 — Integration
- Repository: Both
- Điều kiện: UI-5.1.6.6; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Kiểm thử Android–API–DB, restart, nhiều trang, lỗi mạng, refresh JWT, đổi tài khoản.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Lịch sử tồn tại sau restart; 401/404 đúng, regression pass.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.
