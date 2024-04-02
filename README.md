# 커뮤니티 사이트

## 📃 프로젝트 정보
### 스프링부트를 활용한 커뮤니티 사이트 (23.10.18 ~ )
- 사이트 바로가기 : http://ec2-54-211-11-22.compute-1.amazonaws.com:8080


## 📚 기술 스택
<details>
  <summary>접기/펼치기</summary>

1. Back-end
    - Java
    - Spring Boot
    - h2db
    - mariadb

2. Front-end
    - Html/Css
    - Javascript
    - Thymleaf

3. Cloud
    - aws

</details>

## 📊 ERD
<details>
  <summary>접기/펼치기</summary>

![1](https://github.com/kimjjjj/Algorithm_study/assets/102236761/e8138dbb-6209-40e1-b135-8aac45fd8934)

</details>


## 💻 화면
<details>
  <summary>접기/펼치기</summary>

### 메인 화면
![01](https://github.com/kimjjjj/Algorithm_study/assets/102236761/c1b37323-9c76-4d45-ae1f-eed743b825dc)

### 로그인 화면
![02](https://github.com/kimjjjj/Algorithm_study/assets/102236761/cef478e1-03fc-443a-ab9c-2c8519495938)

### 회원가입 화면
![03](https://github.com/kimjjjj/Algorithm_study/assets/102236761/6d55100d-cf10-487f-a78d-fb31bcb3dc6b)

### 네이버 로그인 화면
![04](https://github.com/kimjjjj/Algorithm_study/assets/102236761/e584ee5e-3477-47f6-9ceb-0fc4a43f45d1)

### 메뉴 선택 화면
![05](https://github.com/kimjjjj/Algorithm_study/assets/102236761/89869ec3-8dec-4c7c-88b5-4d7da283ebb9)

### 글쓰기 화면
![06](https://github.com/kimjjjj/Algorithm_study/assets/102236761/fa86633c-026c-450e-844d-26d034926981)

  <details>
    <summary>이미지 업로드 방법</summary>

#### 이미지 속성 -> 업로드
![07](https://github.com/kimjjjj/Algorithm_study/assets/102236761/eb8ec19c-b799-4296-810f-2fbfd18c3751)

#### 파일 선택 -> 서버로 전송
![08](https://github.com/kimjjjj/Algorithm_study/assets/102236761/cf7f9b7e-764f-4c96-b4b0-ec8818d0c1bb)

#### 확인
![09](https://github.com/kimjjjj/Algorithm_study/assets/102236761/f110e70f-421c-41d9-af2e-f44f9ce19cb0)

  </details>

### 글 검색 화면
![10](https://github.com/kimjjjj/Algorithm_study/assets/102236761/ba40fa75-e619-4ff2-8ce2-1d5a3bd9ff75)

### 회원 정보 변경 화면
![11](https://github.com/kimjjjj/Algorithm_study/assets/102236761/7ebcbb08-63dc-4da9-8ae6-0f27799f4fad)

### 내가 쓴 글 화면
![12](https://github.com/kimjjjj/Algorithm_study/assets/102236761/1c857d68-559a-4eb3-8300-91871a819d42)

</details>


## 💡 오류 해결
<details>
  <summary>접기/펼치기</summary>

1. 로컬에서는 콘솔에서 로그를 보기때문에 문제가 없었지만 aws에 올린 후에는 에러를 보기 어려움
    - logback-spring.xml에 일자별로 로그파일을 생성하도록 추가

2. aws에서 이미지를 불러오지 못하는 현상 발생
    - jar에 포함된 이미지를 불러오지 못해서 workspace의 images 폴더에 이미지 업로드, 이미지 불러오도록 변경
    - file:/// + System.getProperty("user.dir") 활용

</details>

## 🛠️ 남은 과제
<details>
  <summary>접기/펼치기</summary>

~~1. 네이버 로그인 API 개발~~

~~2. 반응형으로 변경~~

3. 라이트/다크모드 개발
4. Jpa 개발

</details>

## 📕 히스토리
<details>
  <summary>접기/펼치기</summary>

2023.12.10
- 마이페이지의 내가 쓴 글, 좋아요 한 글에서 원본 글로 바로가기 기능 추가

2023.12.15
- 게시글에 댓글 기능 추가
- 댓글 좋아요 누르면 유저 포인트에 포인트 추가하도록 기능 추가

2023.12.16
- 게시글 안에서 이전글, 다음글, 목록 이동 기능 추가

2023.12.19
- 유저 프로필 이미지 선택 시 미리보기 기능 추가
- 유저 프로필 이미지 저장 시 이름을 랜덤하게 저장하도록 추가

2023.12.21
- 게시글 수정, 삭제 기능 추가

2023.01.22
- 에디터에 유튜브 링크 기능 추가
- 에디터에 유튜브 링크 저장하면 게시글 목록에서 유튜브 썸네일 보이도록 기능 추가
- mariadb도 사용 가능하도록 추가
- aws 업로드

2024.02.14
- 네이버 로그인 API 개발

2024.02.21
- 반응형으로 개발

</details>
