import { test, expect, request } from '@playwright/test';

// 환경변수
const MCP_PROMPT_URL = process.env.MCP_PROMPT_URL; // 예: http://localhost:3000/mcp/prompt
const MCP_BEARER = process.env.MCP_BEARER; // 필요 시 토큰

test.describe('MCP 프롬프트 요청', () => {
  test('프롬프트 전송 및 200 응답 확인', async ({}) => {
    test.skip(!MCP_PROMPT_URL, 'MCP_PROMPT_URL이 설정되지 않아 테스트를 건너뜁니다.');

    const apiContext = await request.newContext({
      extraHTTPHeaders: MCP_BEARER ? { Authorization: `Bearer ${MCP_BEARER}` } : {},
    });

    const payload = {
      prompt: 'health-check',
      metadata: { source: 'playwright-e2e', ts: Date.now() },
    };

    const resp = await apiContext.post(MCP_PROMPT_URL!, {
      data: payload,
      timeout: 30_000,
    });

    expect(resp.ok()).toBeTruthy();
    const json = await resp.json().catch(() => ({}));
    // 응답에 result 또는 status 등의 기본 키가 있는지 느슨하게 확인
    expect(Object.keys(json).length).toBeGreaterThan(0);
  });
});


