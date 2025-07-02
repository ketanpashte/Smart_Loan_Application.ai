# API Endpoint Testing Guide

## Prerequisites
1. Start the Spring Boot application
2. Ensure database is running and connected
3. Have a valid JWT token for authentication

## Authentication
All endpoints require authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Test Endpoints

### 1. L3 Applications Endpoint
```bash
# Get L3 applications
curl -X GET "http://localhost:8080/api/workflow/l3/applications" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

Expected Response: List of applications with PENDING_L3 status

### 2. Admin Dashboard Endpoints
```bash
# Get dashboard data
curl -X GET "http://localhost:8080/api/admin/dashboard" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"

# Get all applications
curl -X GET "http://localhost:8080/api/admin/applications" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"

# Get all users
curl -X GET "http://localhost:8080/api/admin/users" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

### 3. Document Management Endpoints
```bash
# Upload document (multipart form data)
curl -X POST "http://localhost:8080/api/documents/upload/1" \
  -H "Authorization: Bearer <token>" \
  -F "file=@/path/to/document.pdf" \
  -F "documentType=ITR" \
  -F "description=Income Tax Returns"

# Get documents for application
curl -X GET "http://localhost:8080/api/documents/application/1" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"

# Download document
curl -X GET "http://localhost:8080/api/files/download/document/1" \
  -H "Authorization: Bearer <token>" \
  --output downloaded_document.pdf

# View document (inline)
curl -X GET "http://localhost:8080/api/files/view/document/1" \
  -H "Authorization: Bearer <token>"

# Check required documents
curl -X GET "http://localhost:8080/api/documents/check-required/1" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

### 4. Existing Workflow Endpoints (should still work)
```bash
# RCPU applications
curl -X GET "http://localhost:8080/api/rcpu/applications" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"

# L1 applications
curl -X GET "http://localhost:8080/api/workflow/l1/applications" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"

# L2 applications
curl -X GET "http://localhost:8080/api/workflow/l2/applications" \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json"
```

## Frontend Testing

### 1. Test L3 Dashboard
1. Navigate to `http://localhost:5173/l3/index`
2. Should see applications with PENDING_L3 status
3. Test filtering and search functionality

### 2. Test Admin Sections
1. Navigate to `http://localhost:5173/admin/applications`
2. Should see all applications with filtering options
3. Test document viewer by clicking "Docs" button
4. Navigate to `http://localhost:5173/admin/users`
5. Should see all users with role filtering

### 3. Test Document Upload
1. Navigate to sales new loan application
2. Complete the form and upload documents
3. Verify documents are uploaded to backend
4. Check document viewer functionality

## Database Verification

### Check Documents Table
```sql
-- Verify documents table exists
SELECT * FROM documents;

-- Check document types
SELECT document_type, COUNT(*) FROM documents GROUP BY document_type;

-- Check documents by application
SELECT la.application_id, d.document_type, d.original_file_name 
FROM loan_applications la 
LEFT JOIN documents d ON la.id = d.loan_application_id;
```

### Check Application Status Flow
```sql
-- Check applications by status
SELECT status, COUNT(*) FROM loan_applications GROUP BY status;

-- Check L3 pending applications
SELECT application_id, loan_amount, status FROM loan_applications 
WHERE status = 'PENDING_L3';

-- Check approval history
SELECT la.application_id, ah.stage, ah.decision, ah.created_at
FROM loan_applications la
JOIN approval_history ah ON la.id = ah.loan_application_id
ORDER BY la.application_id, ah.created_at;
```

## Common Issues and Solutions

### 1. L3 Applications Not Showing
- Check if there are applications with loan_amount > 5000000 (50 lakhs)
- Verify L2 approval process is forwarding to L3 correctly
- Check application status in database

### 2. Document Upload Fails
- Verify uploads/documents/ directory exists with write permissions
- Check file size limits (max 5MB)
- Ensure proper authentication token

### 3. Admin Endpoints Return Empty
- Verify user has ADMIN role
- Check database connections
- Verify JWT token is valid

### 4. UI Issues
- Clear browser cache
- Check browser console for JavaScript errors
- Verify API endpoints are accessible

## Performance Testing

### Load Test Document Upload
```bash
# Upload multiple documents simultaneously
for i in {1..5}; do
  curl -X POST "http://localhost:8080/api/documents/upload/1" \
    -H "Authorization: Bearer <token>" \
    -F "file=@test_document_$i.pdf" \
    -F "documentType=ITR" \
    -F "description=Test document $i" &
done
wait
```

### Test Large File Upload
```bash
# Test with larger file (close to 5MB limit)
curl -X POST "http://localhost:8080/api/documents/upload/1" \
  -H "Authorization: Bearer <token>" \
  -F "file=@large_document.pdf" \
  -F "documentType=BANK_STATEMENT" \
  -F "description=Large bank statement"
```

## Security Testing

### Test File Type Validation
```bash
# Try uploading invalid file type
curl -X POST "http://localhost:8080/api/documents/upload/1" \
  -H "Authorization: Bearer <token>" \
  -F "file=@malicious.exe" \
  -F "documentType=ITR" \
  -F "description=Malicious file"
```

### Test Authorization
```bash
# Try accessing without token
curl -X GET "http://localhost:8080/api/admin/applications" \
  -H "Content-Type: application/json"

# Try accessing with invalid token
curl -X GET "http://localhost:8080/api/admin/applications" \
  -H "Authorization: Bearer invalid-token" \
  -H "Content-Type: application/json"
```

## Expected Results

1. **L3 Dashboard**: Should show applications with loan amounts > 50 lakhs
2. **Document Upload**: Should successfully upload and store files
3. **Document Download**: Should return proper file with correct headers
4. **Admin Sections**: Should show real-time data from database
5. **UI Enhancements**: Should display modern, animated interface

All endpoints should return proper HTTP status codes:
- 200: Success
- 400: Bad Request (validation errors)
- 401: Unauthorized (missing/invalid token)
- 403: Forbidden (insufficient permissions)
- 404: Not Found
- 500: Internal Server Error
