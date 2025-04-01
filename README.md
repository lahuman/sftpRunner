# SFTP 파일 다운로드 자동화 (SOLID 리팩토링 적용)

SFTP 서버에서 특정 패턴의 파일을 다운로드하는 과정을 자동화하는 방법을 설명합니다. 또한, SOLID 원칙을 적용하여 코드를 리팩토링하였으며, Docker를 활용한 테스트 환경 구축 방법도 다룹니다.

## 1. 프로젝트 개요
이 프로젝트는 SFTP 서버에서 특정 패턴을 가진 파일을 자동으로 다운로드하는 Java 프로그램입니다.
기존 단일 클래스 방식에서 SOLID 원칙을 적용하여 역할을 분리하였으며, 설정을 외부 파일(`config.properties`)로 관리하도록 개선했습니다.

## 2. 주요 변경 사항
- **SOLID 원칙 적용**: 서비스 계층과 설정 관리를 분리하여 유지보수성을 향상
- **설정 파일 (`config.properties`) 추가**: SFTP 서버 정보 및 다운로드 패턴을 동적으로 변경 가능
- **파일 관리 기능 분리**: `LockFileManager`와 `DownloadHistoryManager`를 도입하여 동시 실행 방지 및 중복 다운로드 방지 기능 강화

## 3. 프로젝트 구조
```
io/github/lahuman/
├── config/
│   ├── SftpConfig.java    # 설정 로드 클래스
├── manager/
│   ├── LockFileManager.java       # 실행 중복 방지 기능
│   ├── DownloadHistoryManager.java # 다운로드 이력 관리 기능
├── service/
│   ├── SftpService.java           # SFTP 연결 및 파일 다운로드 기능
│   ├── SftpDownloaderService.java # 다운로드 프로세스 관리
├── SftpDownloader.java   # 실행 메인 클래스
└── config.properties  # 설정 파일 (SFTP 정보 및 파일 패턴 등)
```

## 4. 설정 파일 (`config.properties`)
```properties
sftp.host=example.com
sftp.port=22
sftp.username=batch
sftp.password=batch123
sftp.remoteDir=/remote/dir/
sftp.filePattern=WE\.ARE\.\d{6}\.DAT
```

## 5. 실행 방법
### 5.1. Docker를 이용한 테스트 SFTP 서버 구축
테스트용 SFTP 서버를 실행하기 위해 다음 명령어를 사용합니다:
```bash
docker run --name test-ssh -p 2222:2222 \
    -e PUID=1000 -e PGID=1000 -e TZ=Asia/Seoul \
    -e USER_PASSWORD="batch123" -e USER_NAME="batch" \
    -e PASSWORD_ACCESS=true -d linuxserver/openssh-server
```

### 5.2. 프로젝트 빌드 및 실행
#### 1) 컴파일
```bash
javac -cp .:lib/jsch-0.2.24.jar -d out $(find io -name "*.java")
```

#### 2) 실행
```bash
java -cp .:lib/jsch-0.2.24.jar:out io.github.lahuman.SftpDownloader config.properties
```

## 6. 결론
Docker를 활용하여 로컬에서 간편하게 SFTP 서버를 구축하고 테스트할 수 있도록 개선하였습니다.
이를 활용하면 다양한 SFTP 파일 전송 자동화 작업을 안정적으로 수행할 수 있습니다.

