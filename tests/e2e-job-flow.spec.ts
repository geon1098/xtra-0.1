import { test, expect } from '@playwright/test';
import path from 'path';

const BASE_USER = { username: 'geon', password: 'asdasd1!' };

async function tryLogin(page) {
  await page.goto('/user/login');
  await page.fill('input#username', BASE_USER.username);
  await page.fill('input#password', BASE_USER.password);
  // 로그인 폼 제출 버튼: 네비게이션의 로그아웃 버튼과 충돌하지 않도록 컨테이너 범위 지정
  const submitBtn = page.locator('.login-container form button[type="submit"]');
  await expect(submitBtn).toBeVisible();
  await Promise.all([
    page.waitForNavigation({ waitUntil: 'domcontentloaded' }),
    submitBtn.click(),
  ]);
  const loginError = await page.locator('text=아이디 또는 비밀번호가 올바르지 않습니다.').first().isVisible().catch(() => false);
  return !loginError;
}

async function ensureUserExistsAndLogin(page) {
  const loggedIn = await tryLogin(page);
  if (loggedIn) return;

  // 회원가입 시도
  await page.goto('/user/signup');
  await page.fill('input[name="username"]', BASE_USER.username);
  await page.fill('input[name="password1"]', BASE_USER.password);
  await page.fill('input[name="password2"]', BASE_USER.password);
  await page.fill('input[name="nickname"]', '지온');
  await page.fill('input[name="email"]', `geon_${Date.now()}@example.com`);
  await page.fill('input[name="phone"]', '010-0000-0000');
  // 회원가입 폼 제출 버튼 스코프 지정
  const signupSubmit = page.locator('.signup-container form button[type="submit"]');
  await expect(signupSubmit).toBeVisible();
  await Promise.all([
    page.waitForLoadState('domcontentloaded'),
    signupSubmit.click(),
  ]);

  // 가입 후 로그인 재시도
  const reloginOk = await tryLogin(page);
  expect(reloginOk).toBeTruthy();
}

test('로그인 → 구인 등록 → 메인으로 돌아가 일반 구인 클릭', async ({ page }) => {
  // 홈 진입
  await page.goto('/');

  // 로그인 페이지로 이동 (네비게이션 우측 링크)
  const loginLink = page.locator('a.custom-navbar-link', { hasText: '로그인' });
  if (await loginLink.isVisible()) {
    await Promise.all([
      page.waitForNavigation({ waitUntil: 'domcontentloaded' }),
      loginLink.click(),
    ]);
  } else {
    await page.goto('/user/login');
  }

  // 사용자 확보 후 로그인
  await ensureUserExistsAndLogin(page);

  // 메인으로 이동 후 "구인 등록" 클릭
  await page.goto('/');
  await expect(page.locator('h2.board-title')).toHaveText('무료 구인');
  await Promise.all([
    page.waitForNavigation({ waitUntil: 'domcontentloaded' }),
    page.click('a.create-btn'),
  ]);

  // 구인 등록 폼 작성 (필수 필드 위주)
  const now = Date.now();
  const siteName = `자동화 현장 ${now}`;
  const title = `자동화 등록 제목 ${now}`;
  await page.fill('input[name="siteName"]', siteName);
  await page.fill('input[name="title"]', title);
  await page.fill('input[name="benefits"]', '중식 제공, 교통비');
  await page.fill('input[name="jobDescription"]', '자재 정리 및 현장 보조 업무');
  await page.fill('input[name="location"]', '서울특별시 테스트구 테스트동');

  // 콤보박스(셀렉트) 값 설정
  await page.selectOption('select[name="industry"]', { label: '오피스텔' });
  await page.selectOption('select[name="gender"]', { label: '무관' });
  await page.selectOption('select[name="experience"]', { label: '1년 이상' });
  await page.selectOption('select[name="jobType"]', { label: '정규직' });
  await page.selectOption('select[name="age"]', { label: '직접입력' });
  await page.fill('#ageInput', '45');
  await page.selectOption('select[name="workNumber"]', { label: '2명' });
  await page.selectOption('select[name="salary"]', { label: '기본급' });

  // 기타 필드(선택)
  await page.fill('input[name="cPerson"]', '홍길동');
  await page.fill('input[name="phone"]', '010-1234-5678');
  await page.fill('input[name="jobContent"]', '현장 전반 지원');
  await page.fill('input[name="deadDate"]', '2025-12-31');
  await page.fill('input[name="jobWork"]', '근면 성실');
  await page.fill(
    'textarea[name="jobDetails"]',
    [
      '업무 상세:',
      '- 자재 하역 및 정리',
      '- 현장 청소 및 안전 수칙 준수',
      '- 팀원과의 의사소통 원활',
      '근무 조건:',
      '- 주 5일, 08:00 ~ 17:00',
      '- 중식 제공, 교통비 일부 지원',
      '우대 사항:',
      '- 관련 경력 1년 이상',
      '- 성실하고 책임감 있는 분',
    ].join('\n')
  );

  // 현장 이미지 첨부: 업로드 영역 클릭 → 파일 선택
  const imagePath = path.join(process.cwd(), 'cypress', 'fixtures', 'test-image.jpg');
  await page.click('#imageUploadArea');
  await page.setInputFiles('#imageInput', imagePath);
  await expect(page.locator('#imagePreview .preview-item').first()).toBeVisible();

  // 지도 등록: 주소 입력 필드에 직접 작성
  await page.evaluate(() => {
    const addr = document.getElementById('address') as HTMLInputElement | null;
    if (addr) addr.removeAttribute('readonly');
  });
  await page.click('#address');
  await page.fill('#address', '서울특별시 강남구 테헤란로 1');

  // 구인 등록 폼 제출 버튼 스코프 지정
  const workSubmit = page.locator('.work-form-container form button[type="submit"]');
  await expect(workSubmit).toBeVisible();
  await Promise.all([
    page.waitForNavigation({ waitUntil: 'domcontentloaded' }),
    workSubmit.click(),
  ]);

  // 메인으로 이동 후 일반 구인 중 하나 클릭
  await page.goto('/');
  const jobLink = page.locator('table.job-table a.job-title', { hasText: siteName }).first();
  await expect(jobLink).toBeVisible();
  await Promise.all([
    page.waitForNavigation({ waitUntil: 'domcontentloaded' }),
    jobLink.click(),
  ]);

  // 상세 페이지 진입 확인 (제목 혹은 경로 기반 가벼운 검증)
  await expect(page).toHaveURL(/\/work\/detail\//);
});


