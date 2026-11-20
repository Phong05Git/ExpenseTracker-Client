# G2 — Local Infrastructure và TLS
Chỉ thực hiện một mã mỗi lượt. Đọc AGENTS.md và PROGRESS.md; kiểm tra gate trước, build/test phù hợp và ghi bằng chứng. Các bước khảo sát/thiết kế không bắt buộc sửa code; không tạo migration/deploy thật nếu chưa duyệt.

## G2.01 — Local DB inventory
- Repository: Backend
- Điều kiện: UI-5.1.6.7; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Xác minh PostgreSQL local, cấu hình theo môi trường, quyền DB tối thiểu.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Kết nối DB test thành công, không lộ secrets.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G2.02 — Backup & restore
- Repository: Backend
- Điều kiện: G2.01; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Soạn quy trình backup và khôi phục vào DB cô lập, đối chiếu số lượng và ràng buộc.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Restore thử thành công; không ghi đè DB thật.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G2.03 — Local hosting
- Repository: Backend
- Điều kiện: G2.02; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Cấu hình Kestrel, bind interface, port, development/release và firewall theo topology được duyệt.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Health/API hoạt động từ host và mạng được phép; DB không mở công khai.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G2.04 — TLS design
- Repository: Both
- Điều kiện: G2.03; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: So sánh private CA/tự ký và phương án CA công cộng nếu phù hợp domain; xác định SAN, hostname, trust chain, rotation.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Người dùng duyệt topology và mô hình trust trước khi cấp cert.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G2.05 — Certificate provisioning
- Repository: Backend
- Điều kiện: G2.04; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Tạo hoặc cấp cert theo phương án duyệt, bảo vệ private key, hướng dẫn gia hạn.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Kiểm tra SAN, chain, expiry, quyền file; không commit key.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G2.06 — HTTPS hosting
- Repository: Backend
- Điều kiện: G2.05; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Cấu hình HTTPS Kestrel và chính sách HTTP theo môi trường.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Client tin cậy kết nối đúng hostname, cert sai bị từ chối.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G2.07 — Base URL
- Repository: Android
- Điều kiện: G2.06; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Tách URL theo môi trường, emulator và thiết bị thật; loại Render khỏi cấu hình local.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Thiết bị kết nối HTTPS đúng host; không nhầm localhost.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G2.08 — Trust anchor
- Repository: Android
- Điều kiện: G2.07; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Cấu hình CA trust phạm vi cần thiết; giữ hostname verification.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Cert không tin cậy và hostname sai bị từ chối.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G2.09 — Certificate pinning
- Repository: Android
- Điều kiện: G2.08; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Kế thừa pinning hiện có, SPKI chính/dự phòng theo thiết kế.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Pin đúng pass, pin sai fail, không trust-all hay bypass release.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G2.10 — Rotation
- Repository: Both
- Điều kiện: G2.09; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Diễn tập thay cert/key với pin dự phòng trên môi trường test.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: Kết nối qua giai đoạn chuyển tiếp, rollback được.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.

## G2.11 — MITM & deployment
- Repository: Both
- Điều kiện: G2.10; xác minh tiến độ và phê duyệt nếu cần.
- Mục tiêu và triển khai: Thử proxy MITM và kiểm tra cleartext, viết runbook start/stop/recovery.
- Bảo mật: tuân thủ AGENTS.md; dùng fixture giả, không giảm kiểm soát bảo mật. Không tự thay đổi API/schema hoặc cấu hình thật.
- Nghiệm thu: MITM sai trust/pin thất bại, kết nối hợp lệ pass; tài liệu tái lập được.
- Xác minh: ghi lệnh build/test hoặc bằng chứng khảo sát/thiết kế phù hợp; ghi kết quả thật, blocker, diff và commit gốc vào PROGRESS.md. Nếu không chạy được, ghi chưa kiểm thử và dừng tại gate.
