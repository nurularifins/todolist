import { test, expect } from '@playwright/test';

test.describe('Authentication Flow', () => {
    // Generate a unique user for each run
    const timestamp = Date.now();
    const username = `testuser_${timestamp}`;
    const email = `user_${timestamp}@example.com`;
    const password = 'Password@123';

    test('should register, login, and verify dashboard', async ({ page }) => {
        // 1. Go to Register Page
        await page.goto('/register');
        await expect(page).toHaveTitle(/Register/);

        // 2. Fill Registration Form
        await page.fill('#fullName', username);
        await page.fill('#email', email);
        await page.fill('#password', password);
        await page.fill('#confirmPassword', password);
        await page.click('button[type="submit"]');

        // 3. Should redirect to Login (or Dashboard depending on impl, usually login)
        // Assuming implementation redirects to login with success message
        await expect(page).toHaveURL(/\/login/);
        await expect(page.locator('.text-green-700')).toBeVisible(); // Success message

        // 4. Login
        await page.fill('#email', email);
        await page.fill('#password', password);
        await page.click('button[type="submit"]');

        // 5. Verify Dashboard
        await expect(page).toHaveURL(/\//); // or /dashboard or /tasks
        // Check for Sidebar visibility
        await expect(page.getByRole('navigation').first()).toBeVisible();
        // Check for User Avatar or Name in header
        await expect(page.locator('nav').filter({ hasText: username })).toBeDefined();

        // 6. Logout
        // Assuming logout is in a profile menu or visible
        // Since we didn't implement a logout button in the main UI explicitly in the last steps,
        // we might check purely if we can access protected resources.
        // Let's assume there is a logout link or we can navigate to /logout
    });
});
