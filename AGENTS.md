# ExpenseTracker Development Instructions

## Project
ExpenseTracker is a student Android personal finance application.

Backend:
- .NET 10 Web API
- EF Core 10
- PostgreSQL
- Render deployment
- Clean Architecture

Android:
- Java
- Android Studio
- Hilt
- Retrofit 3
- OkHttp 4.12
- DataStore
- Tink
- MPAndroidChart

## Architecture Rules
- Preserve the existing Clean Architecture.
- Do not move responsibilities between Domain, Application, Infrastructure and API without a strong reason.
- Do not introduce unnecessary dependencies.
- Do not rewrite unrelated files.
- Before modifying a file, inspect the existing implementation and its callers/dependencies.
- Preserve existing behavior unless the task explicitly requires changing it.

## Security Rules
- Never log passwords, password hashes, access tokens, refresh tokens, Authorization headers, JWT secrets, database passwords or API keys.
- Keep server-side validation even when client-side validation is added.
- Do not remove authentication, authorization, rate limiting, certificate pinning, FLAG_SECURE, token encryption, root detection or session controls.
- Do not add token replay/deduplication logic for notification transactions unless explicitly requested.
- System categories have UserId = null.
- User categories have UserId assigned to the owner.
- Never allow one user to access another user's financial data.

## Coding Rules
- Use modern .NET 10 / Android APIs.
- Java code: no unnecessary line breaks.
- Short statements should remain on one line.
- Break lines only when a statement is genuinely long or difficult to read.
- Do not use auto in C++.
- For Android/Backend changes, provide complete final files when requested.
- Avoid speculative fixes.
- Do not change names or architecture unnecessarily.

## Workflow
Before changing code:
1. Inspect relevant files and dependencies.
2. Identify the root cause.
3. Modify only necessary files.
4. Build/test the affected project.
5. Report exactly what changed and what passed/failed.

After each completed feature:
- Do not automatically modify unrelated code.
- Keep the repository buildable.