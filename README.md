# 🧴 피부 첫 걸음 🧴

<img width="1060" alt="image" src="https://github.com/user-attachments/assets/9c337911-532f-4d69-bc30-296fc4404aa2">

## 🙋 프로젝트 제안 개요

### 아이디어 선정 이유 
<img width="859" alt="image" src="https://github.com/user-attachments/assets/41219bef-0c5a-458b-9807-6970627e00b2">

### 기존 서비스의 한계점
- 피부 진단 과정에서 수 십 가지의 설문 & 응답 형식 존재<br> 
    ➡️ 주관적인 결과 도출 및 진단이 많이 시간 소요됨
- 스킨 케어 제품을 자사몰을 통해서 판매<br>
    ➡️ 자사몰의 가격 정보만 드러나기에 최저가 비교 불가능. 합리적 소비의 어려움이 생길 수 있음

### 기존 서비스와의 차별점
- **AI를 활용한** 빠르고 정확한 여러가지 피부 진단 서비스 제공<br>
  ➡️기존 서비스의 과도한 설문 절차 삭제로 인한 빠른 결과 도출, 주관을 배제한 객관적 결과 도출
- 오픈 마켓을 이용한 **최저가 비교** 서비스 제공<br>
  ➡️자사몰이 아닌 오픈 마켓 최저가 비교를 통한 합리적인 소비


## 🛠 개발 내용

### 사용자 기능

<img width="1057" alt="image" src="https://github.com/user-attachments/assets/1d4220d9-6c29-47fb-b73d-2c60c05e8d21">

- **사용자 인증**: OAuth 2.0을 이용한 회원가입 및 로그인 기능

- **AI 피부 진단**: AI를 이용해 사용자의 피부 상태를 진단. 피부 타입은 normal, oily, dry로 분류. 피부 트러블은 acne, redness, normal로 분류. 퍼스널 컬러는 spring, summer, autumn, winter로 분류

- **맞춤 화장품 추천**: 네이버 쇼핑 API를 이용해 사용자의 피부 상태에 맞는 화장품을 추천

- **화장품 리뷰**: 앱 내에서 화장품 리뷰를 작성하고 다른 사용자들과 공유

- **자가진단**: 사용자가 캘린더 형태로 되어있는 자신의 피부 진단 기록을 조회

### 관리자 기능

<img width="1060" alt="image" src="https://github.com/user-attachments/assets/213690d9-1ba8-4cc1-899a-767648ccfa7c">

- **회원 관리**: 회원 정보를 조회하고 회원의 피부 진단 결과를 확인

- **화장품 카테고리 관리**: 화장품 검색 필터링을 위한 카테고리를 관리

- **관리자 인증**: 관리자 권한을 가진 사용자만 접근 가능한 페이지

- **AI 모델 관리**: 3개의 모델을 버전 별로 관리하며 진단 데이터를 기반으로 모델 재학습 후 대체 여부 결정

## 📚️ 기술 스택

### 프론트엔드 (Frontend)

#### 앱 (App)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-000000?style=for-the-badge&logo=ios&logoColor=white)
![Flutter](https://img.shields.io/badge/Flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white)

#### 웹 (Web)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)


### 백엔드 (Backend)
![Linux](https://img.shields.io/badge/Linux-FCC624?style=for-the-badge&logo=linux&logoColor=black)
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-0769AD?style=for-the-badge&logo=custom&logoColor=white)
![OAuth 2.0](https://img.shields.io/badge/OAuth%202.0-4285F4?style=for-the-badge&logo=oauth&logoColor=white)
![TensorFlow API](https://img.shields.io/badge/TensorFlow%20API-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)


### 인공지능 (AI)
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![TensorFlow](https://img.shields.io/badge/TensorFlow-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white)

### 데이터 (Data)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Naver OpenAPI](https://img.shields.io/badge/Naver%20OpenAPI-03C75A?style=for-the-badge&logo=naver&logoColor=white)


## 🛠 개발 환경

### 서버 (Server)

| 구분              | 내용                                                                                     |
|-----------------|----------------------------------------------------------------------------------------|
| **OS**          | Rocky Linux 9.3 (=RHEL)                                                                |
| **편집 툴**        | IntelliJ IDEA 2023.2.4 (Ultimate Edition)                                              |
| **프레임워크**       | Spring Boot 3.2.4                                                                      |
| **개발 도구**       | JDBC, Gradle, TensorFlow, Spring Data JPA, Tomcat, Lombok, OAuth 2.0, Log4j2, QueryDSL |
| **DB**          | MariaDB                                                                                |
| **CPU**         | Intel i9 18 Core                                                                       |
| **RAM**         | 124GB                                                                                  |
| **DISK**        | 7TB (RAID1)                                                                            |
| **Network**     | 100Mbps                                                                                |
| **TCP Port 주소** | 60022                                                                                  |
| **IP 주소**       | [ceprj.gachon.ac.kr](http://ceprj.gachon.ac.kr)                                        |

### 클라이언트 (Client)

| 구분          | 내용               |
|-------------|------------------|
| **OS**      | Android OS / iOS |
| **개발 IDE**  | Android Studio   |
| **CPU**     | Intel i9 18 Core |
| **RAM**     | 124GB            |
| **DISK**    | 7TB (RAID1)      |
| **Network** | 100Mbps          |

