커뮤니티 사이트
--


📃 프로젝트 정보
--
스프링부트를 활용한 커뮤니티 사이트

사이트 바로가기 : http://ec2-54-211-11-22.compute-1.amazonaws.com:8080


📚 기술 스택
--
1. Back-end
    - Java
    - Spring Boot
    - h2db
    - mariadb

2. Front-end
    - Html/Css/Javascript
    - Thymleaf
    - Jquery

3. Cloud
    - awe


📊 ERD
--
<details>
  <summary>접기/펼치기</summary>

![1](https://github.com/kimjjjj/Algorithm_study/assets/102236761/6ebf44d5-b6ac-494b-b344-8e031294fa0f)

</details>


💡 오류 해결
--
1. 로컬에서는 콘솔에서 로그를 보기때문에 문제가 없었지만 awe에 올린 후에는 에러를 보기 어려움
    - logback-spring.xml에 일자별로 로그파일을 생성하도록 추가

2. awe에서 이미지를 불러오지 못하는 현상 발생
    - jar에 포함된 이미지를 불러오지 못해서 workspace의 images 폴더에 이미지 업로드, 이미지 불러오도록 변경
    - file:/// + System.getProperty("user.dir") 활용

3. 에디터로 사용한 ckeditor가 awe에서 cors에러 발생
    - ckeditor를 cdn으로 사용하는 방법에서 설치하여 사용하는 방법으로 변경


🛠️ 남은 과제
--
1. 네이버 로그인 API 개발
2. 반응형으로 변경
3. 라이트/다크모드 개발
4. Jpa 개발
