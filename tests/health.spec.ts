import { test, expect } from '@playwright/test';

test('홈 페이지가 로드되고 히어로 타이틀이 보인다', async ({ page }) => {
  await page.goto('/');
  await expect(page.locator('h1.hero-title')).toHaveText(/XTRA/i);
});


