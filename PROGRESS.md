# PROGRESS — cập nhật ban đầu 2026-11-19

## Baseline và minh chứng
- UI-5.1.1–5.1.3: hoàn thành theo lịch sử dự án; Android models/DTOs build OK, migration AddChatConversationHistory đã apply local theo xác nhận trước đó.
- UI-5.1.4–5.1.5: người dùng xác nhận build, 4 API chat hoạt động và authentication, IDOR, mass assignment, input validation, session metadata update đều OK. Đây là kết quả do người dùng báo cáo, không phải test tự động mới chạy.
- UI-5.1.6.1: khảo sát source Android hoàn thành. UI-5.1.6.2: người dùng xác nhận build OK và push GitHub. ZIP Android khảo sát chưa có ChatApi.java (patch riêng có); cần xác minh Git HEAD trước khi tiếp tục.
- Local PostgreSQL đã dùng cho chat; TLS local, Admin, AI và các kiểm thử tổng thể chưa có đủ bằng chứng hoàn thành. Không đánh dấu hoàn thành chỉ vì có file.
- Không có URL/commit SHA GitHub được cung cấp; chưa xác minh remote hoặc chạy build/test trong môi trường tạo tài liệu.

## Nhiệm vụ tiếp theo (roadmap v2)
- G0.01–G0.04: xác minh baseline; ghi phần việc của repo này, phối hợp repo kia.
- Sau G0 và phê duyệt: UI-5.1.6.3, sau đó .4, .5, .6, .7 theo gate.
- G2–G7: chưa xác minh/triển khai đầy đủ; không đánh dấu hoàn thành.

## Mẫu nhật ký (thêm một mục mỗi nhiệm vụ)
- Ngày / mã nhiệm vụ / commit gốc:
- Trạng thái:
- File thay đổi và lý do:
- Lệnh build/test và kết quả thực tế (hoặc lý do chưa chạy):
- Security/regression evidence:
- Thay đổi API/schema/dependency:
- Blocker/quyết định cần duyệt:
- Bước tiếp theo:
