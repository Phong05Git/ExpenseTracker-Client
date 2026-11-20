# ROADMAP — ExpenseTracker Phase 2 (v2)
Roadmap chung cho mọi repo. G0–G7 là thứ tự mới, thay bảng G0–G6 cũ; giữ mã UI-5.1.x. Không tự mở rộng phạm vi. G0 là bước xác minh trước khi tiếp tục UI; mỗi giai đoạn chỉ bắt đầu khi gate trước đạt và người dùng duyệt. Both = Android + Backend; Portal = repo/vị trí chưa được quyết định. Với nhiệm vụ liên repo, chỉ sửa repo đang mở, ghi handoff và chờ evidence repo kia.
Nguồn: Project Baseline, bộ hướng dẫn cũ và xác nhận build/test ngày 2026-11-19. G2–G7 là kế hoạch, không phải xác nhận đã triển khai.
## Trạng thái đã ghi nhận
UI-5.1.1–UI-5.1.6.2: hoàn thành theo xác nhận người dùng; cần đối chiếu HEAD tại G0. Local PostgreSQL đã dùng; chưa xác minh TLS/Admin/AI.
## Danh mục nhiệm vụ

### [G0 — Xác minh baseline](docs/phases/G0_BASELINE.md)
| Mã | Chủ trì | Nhiệm vụ |
|---|---|---|
| G0.01 | Both | Git baseline |
| G0.02 | Both | Dependency |
| G0.03 | Backend | Database baseline |
| G0.04 | Both | Regression baseline |

### [G1 — Hoàn thiện chat persistence](docs/phases/G1_CHAT_PERSISTENCE.md)
| Mã | Chủ trì | Nhiệm vụ |
|---|---|---|
| UI-5.1.6.3 | Android | Repository & Mapper |
| UI-5.1.6.4 | Android | Use Cases & DI |
| UI-5.1.6.5 | Android | ViewModel |
| UI-5.1.6.6 | Android | UI Integration |
| UI-5.1.6.7 | Both | Integration |

### [G2 — Local Infrastructure và TLS](docs/phases/G2_LOCAL_TLS.md)
| Mã | Chủ trì | Nhiệm vụ |
|---|---|---|
| G2.01 | Backend | Local DB inventory |
| G2.02 | Backend | Backup & restore |
| G2.03 | Backend | Local hosting |
| G2.04 | Both | TLS design |
| G2.05 | Backend | Certificate provisioning |
| G2.06 | Backend | HTTPS hosting |
| G2.07 | Android | Base URL |
| G2.08 | Android | Trust anchor |
| G2.09 | Android | Certificate pinning |
| G2.10 | Both | Rotation |
| G2.11 | Both | MITM & deployment |

### [G3 — Admin Backend và RBAC](docs/phases/G3_ADMIN_BACKEND.md)
| Mã | Chủ trì | Nhiệm vụ |
|---|---|---|
| G3.01 | Backend | Permission matrix |
| G3.02 | Backend | RBAC schema |
| G3.03 | Backend | Admin bootstrap |
| G3.04 | Backend | Admin auth |
| G3.05 | Backend | Authorization policies |
| G3.06 | Backend | User listing |
| G3.07 | Backend | Account lock |
| G3.08 | Backend | Account activation |
| G3.09 | Backend | Role management |
| G3.10 | Backend | Session revocation |
| G3.11 | Backend | System categories |
| G3.12 | Backend | Audit log |
| G3.13 | Backend | Dashboard |
| G3.14 | Backend | Admin security |

### [G4 — Admin Portal](docs/phases/G4_ADMIN_PORTAL.md)
| Mã | Chủ trì | Nhiệm vụ |
|---|---|---|
| G4.01 | Portal | Stack decision |
| G4.02 | Portal | Scaffold |
| G4.03 | Portal | Login/session |
| G4.04 | Portal | Dashboard UI |
| G4.05 | Portal | User management UI |
| G4.06 | Portal | Role UI |
| G4.07 | Portal | Category UI |
| G4.08 | Portal | Audit viewer |
| G4.09 | Portal+Backend | Portal verification |

### [G5 — Local AI Infrastructure](docs/phases/G5_LOCAL_AI.md)
| Mã | Chủ trì | Nhiệm vụ |
|---|---|---|
| G5.01 | Backend | Hardware/runtime |
| G5.02 | Backend | Model benchmark |
| G5.03 | Backend | Local runtime |
| G5.04 | Backend | Provider abstraction |
| G5.05 | Backend | Orchestration |
| G5.06 | Backend | Financial aggregation |
| G5.07 | Backend | Context builder |
| G5.08 | Backend | AI infrastructure gate |

### [G6 — AI Chatbot và bảo mật AI](docs/phases/G6_AI_SECURITY.md)
| Mã | Chủ trì | Nhiệm vụ |
|---|---|---|
| G6.01 | Both | AI API contract |
| G6.02 | Backend | Conversation context |
| G6.03 | Backend | Assistant persistence |
| G6.04 | Backend | Financial advice |
| G6.05 | Android | Android AI integration |
| G6.06 | Backend | Prompt boundaries |
| G6.07 | Both | Input/output controls |
| G6.08 | Backend | Data leakage |
| G6.09 | Backend | Resource abuse |
| G6.10 | Both | AI quality & security gate |

### [G7 — Tổng kiểm thử và nghiệm thu](docs/phases/G7_FINAL_TESTING.md)
| Mã | Chủ trì | Nhiệm vụ |
|---|---|---|
| G7.01 | Both | Core regression |
| G7.02 | Portal+Backend | Admin regression |
| G7.03 | Both | Transport security |
| G7.04 | Both | AI security regression |
| G7.05 | Both | Performance |
| G7.06 | Backend | Recovery |
| G7.07 | Both | Documentation |
| G7.08 | Both | Acceptance |

## Ánh xạ mã cũ
G0 cũ → G0; G1.1–G1.3 cũ → G2; G2.x → G3; G3.x → G4; G4.x → G5; G5.x → G6; G6 cũ → G7. Không dùng mã cũ cho nhiệm vụ mới.

## Gate chung
Mỗi nhiệm vụ phải ghi trạng thái, evidence, dependency và handoff. Không tự chuyển phase, apply DB thật, push/merge, thay cert, tải model lớn hay quyết định stack. Gate liên repo chỉ đạt khi đủ evidence của mọi repo liên quan.
