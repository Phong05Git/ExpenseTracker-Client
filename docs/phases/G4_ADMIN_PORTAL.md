# G4 — Admin Portal
Chỉ thực hiện một mã mỗi lượt. Đọc AGENTS.md và PROGRESS.md; kiểm tra gate trước, build/test phù hợp và ghi bằng chứng. Các bước khảo sát/thiết kế không bắt buộc sửa code; không tạo migration/deploy thật nếu chưa duyệt.

## G4.01 — Stack decision
- Repository: Portal
- Điều kiện: G3.14; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Đề xuất stack, repo, auth storage, threat model; không tự chọn framework.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Người dùng duyệt stack và vị trí repo.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G4.02 — Scaffold
- Repository: Portal
- Điều kiện: G4.01; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Khởi tạo project, routing, môi trường và client API.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Build, không secrets, HTTPS phù hợp.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G4.03 — Login/session
- Repository: Portal
- Điều kiện: G4.02; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Đăng nhập, logout, expiry, xử lý 401/403; CSRF/XSS theo auth design.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Test phiên, không log token, không dựa UI làm authorization.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G4.04 — Dashboard UI
- Repository: Portal
- Điều kiện: G4.03; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Hiển thị aggregate, loading/error/empty.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Khớp API, không chế số liệu.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G4.05 — User management UI
- Repository: Portal
- Điều kiện: G4.04; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Danh sách/lọc, khóa/mở, activate/deactivate, revoke.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Kiểm thử thao tác, xác nhận hành động nhạy cảm.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G4.06 — Role UI
- Repository: Portal
- Điều kiện: G4.05; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Hiển thị quyền và cấp/thu hồi theo API đã duyệt.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: UI xử lý forbidden; không bypass trực tiếp API.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G4.07 — Category UI
- Repository: Portal
- Điều kiện: G4.06; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Danh mục hệ thống, thêm/sửa/activate/deactivate.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Không ảnh hưởng lịch sử giao dịch.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G4.08 — Audit viewer
- Repository: Portal
- Điều kiện: G4.07; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Danh sách audit phân trang/lọc theo API và quyền.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Không lộ bí mật, escape nội dung không tin cậy.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G4.09 — Portal verification
- Repository: Portal+Backend
- Điều kiện: G4.08; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Test UI, API trực tiếp, XSS/CSRF khi phù hợp, session và RBAC.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Build/test và bằng chứng quyền; không tự deploy.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.
