# AGENTS.md — ExpenseTracker Phase 2

## Quy tắc bắt buộc
- Đọc ROADMAP.md và PROGRESS.md trước khi sửa. Kiểm tra git status, commit hiện tại, mã nguồn và dependency thực tế; Git là căn cứ implementation cuối cùng. Không ghi đè thay đổi của người dùng.
- Chỉ làm một mã công việc được giao trong mỗi lượt. Nếu không có mã, chọn bước chưa hoàn thành đầu tiên có đủ điều kiện trong PROGRESS.md. Không tự chuyển sang giai đoạn tiếp theo.
- Trước khi sửa: nêu mục tiêu, file dự kiến, hợp đồng API, rủi ro. Sau khi sửa: xem diff, build, chạy test phù hợp, cập nhật PROGRESS.md với lệnh, kết quả thật, file thay đổi, blocker và bước tiếp theo. Không đánh dấu PASS nếu chưa chạy hoặc chưa có xác nhận của người dùng.
- Kế thừa kiến trúc hiện tại; không rewrite, nâng cấp hàng loạt, thêm dependency nếu chưa kiểm tra tương thích và nhu cầu. Không đổi API/schema ngoài phạm vi; thay đổi hợp đồng phải được phối hợp giữa hai repo và có kế hoạch tương thích.
- Không đọc/ghi bí mật ngoài nhu cầu được phép; không đưa password, JWT, private key, connection string hoặc dữ liệu tài chính thật vào code, log, fixture, prompt hay commit. Dùng cấu hình môi trường và dữ liệu giả lập.
- Không tự deploy, push, merge, sửa lịch sử git, áp migration lên DB thật, xóa dữ liệu, thay cert hoặc mở cổng mạng nếu chưa được phê duyệt rõ ràng. Không tắt kiểm tra TLS/pinning/auth để vượt test. Nếu môi trường không đủ, dừng tại blocker và đưa lệnh cho người dùng chạy.
- Authorization và ownership do Backend thực thi; không tin UserId/role từ client. AI không truy cập DB trực tiếp, chỉ nhận dữ liệu tối thiểu đã giới hạn theo người dùng; không dùng system prompt làm ranh giới phân quyền. AI ban đầu chỉ tư vấn đọc dữ liệu, không thực hiện giao dịch.
- Không mở rộng sang thanh toán, blockchain, chi tiêu nhóm hay API ngân hàng. Không dùng AI cloud/fallback cloud khi chưa được duyệt.
- Báo cáo tiếng Việt; code theo convention repo. Mỗi lượt kết thúc bằng trạng thái: chưa triển khai / đang triển khai / đã build / đã kiểm thử / hoàn thành; ghi rõ nguồn minh chứng.

## Android
- Java 17, XML/ViewBinding, MVVM/Clean Architecture, RxJava3, Retrofit/OkHttp, Hilt. Kiểm tra app/build.gradle.kts và gradle/libs.versions.toml trước khi đổi dependency.
- Root package hiện quan sát: app/src/main/java/com/example/expensetracker. Tham khảo TransactionRepositoryImpl, TransactionApi, RepositoryModule, NetworkModule và ChatbotFragment; không giả định tên file mới đã tồn tại.
- Dùng chung Retrofit, AuthInterceptor, TokenAuthenticator; không tạo client bỏ qua auth/pinning. Giữ Resource, mapper, use case và lifecycle convention hiện hữu. Không chạy network trên main thread; dispose/cancel đúng lifecycle; không lưu nội dung chat vào log.
- Bảo toàn metadata phân trang, thứ tự tin nhắn; tránh gửi trùng khi nhấn nhiều lần hoặc retry POST; xử lý loading, empty, error, expired auth, chuyển phiên và logout (xóa state cũ, không hiển thị dữ liệu tài khoản trước).
- Build Windows: `./gradlew.bat assembleDebug`; Linux: `./gradlew assembleDebug`. Chạy `testDebugUnitTest` và lintDebug khi phù hợp; kiểm thử thiết bị thật/emulator riêng nếu có. Không coi build là runtime test.

## Hợp đồng chat hiện tại (đối chiếu lại source trước khi sửa)
- GET /api/chat/sessions?page=1&pageSize=20 → danh sách phân trang.
- POST /api/chat/sessions {title} → 201, ChatSessionDto.
- GET /api/chat/sessions/{sessionId}/messages?page=1&pageSize=30 → danh sách phân trang.
- POST /api/chat/sessions/{sessionId}/messages {content} → 201, ChatMessageDto role=user; hiện chưa sinh phản hồi AI.
- Envelope success/message/data; page gồm items/page/pageSize/totalItems/totalPages. Session gồm id/title/createdAt/updatedAt/lastMessagePreview; message gồm id/chatSessionId/role/content/createdAt. Không gửi userId hoặc role. 401 khi thiếu auth, 404 khi không sở hữu session, 400 khi input không hợp lệ.

## Điều phối roadmap v2
- ROADMAP.md và docs/phases/ giống nhau giữa hai repo. Đọc phase chứa mã nhiệm vụ trước khi sửa; dùng mã mới, giữ UI-5.1.x.
- G0 xác minh HEAD trước khi tiếp tục. Chỉ làm task thuộc repo hiện tại; không tự thao tác repo khác. Task Both cần handoff, không tự xác nhận kết quả bên kia. Portal chưa có stack/repo được duyệt.
- Đối với task khảo sát/thiết kế, ghi bằng chứng thay cho build không cần thiết. Gate chưa đạt thì dừng; không đánh dấu hoàn thành.
