FROM jenkins/jenkins:lts-jdk17

USER root

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
      curl gnupg ca-certificates apt-transport-https \
      git xdg-utils \
      libasound2 libatk1.0-0 libatk-bridge2.0-0 libdrm2 libdbus-1-3 \
      libxkbcommon0 libatspi2.0-0 libxcomposite1 libxdamage1 libxfixes3 \
      libxrandr2 libcups2 libnss3 libx11-xcb1 libxcursor1 libxi6 libxtst6 \
      libgbm1 libgtk-3-0 fonts-liberation && \
    rm -rf /var/lib/apt/lists/*

# Install Node.js 18
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get update && apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

# Non-root back
USER jenkins


