# Huong dan test nhanh API

Tai khoan mau duoc tao khi chay app:

- `admin / 123456`
- `employer / 123456`
- `candidate / 123456`

## 1. Dang nhap

`POST /api/v1/auth/login`

```json
{
  "username": "employer",
  "password": "123456"
}
```

Copy `accessToken`, cac API can quyen dung header:

`Authorization: Bearer <accessToken>`

## 2. Employer tao job

`POST /api/v1/employer/jobs`

```json
{
  "title": "Java Intern",
  "description": "Hoc Spring Boot va lam REST API",
  "location": "Ha Noi",
  "salaryMin": 300.0,
  "salaryMax": 500.0,
  "deadline": "2026-12-31"
}
```

Job moi co trang thai `PENDING`.

## 3. Admin duyet job

`PATCH /api/v1/admin/jobs/{jobId}/status?status=OPEN`

Sau khi duyet, candidate/public moi thay job.

## 4. Candidate nop ho so

`POST /api/v1/candidate/applications`

```json
{
  "jobId": 1,
  "coverLetter": "Em muon ung tuyen vi tri nay",
  "cvUrl": "/uploads/cv/demo.pdf"
}
```

Khi nop thanh cong, AOP se ghi log: `Candidate ID ... applied for Job ID ...`.

## 5. Employer cap nhat trang thai ho so

`PATCH /api/v1/employer/applications/{applicationId}/status`

```json
{
  "status": "INTERVIEWING",
  "employerNote": "Hen phong van vong 1"
}
```

## 6. Logout

`POST /api/v1/auth/logout`

Access token hien tai se duoc dua vao Redis blacklist.

## 7. Doi mat khau

`POST /api/v1/auth/change-password`

```json
{
  "oldPassword": "123456",
  "newPassword": "1234567"
}
```

## 8. Quen mat khau

`POST /api/v1/auth/forgot-password`

```json
{
  "username": "candidate",
  "email": "candidate@gmail.com"
}
```

He thong se tao mat khau tam thoi, cap nhat vao tai khoan va gui mat khau do ve email neu da bat `app.mail.enabled=true` va cau hinh SMTP.

## 9. Admin tim kiem va phan trang user

`GET /api/v1/admin/users?keyword=admin&page=0&size=5`

## 10. Admin doi role cho user

Public register chi tao tai khoan `CANDIDATE`. Neu muon user thanh employer/admin, admin goi:

`PATCH /api/v1/admin/users/{userId}/role`

```json
{
  "role": "EMPLOYER",
  "companyName": "Demo Company"
}
```
