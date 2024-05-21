# 우리 동네

## Description

우리에게 익숙한 주변이든 여행을 간 낯선 곳이든, 별 계획없이 하루를 맞이할 때 `뭘하면 좋을까?` 라는 고민을 하면서 많은 시간을 낭비하곤 합니다. 그럼 우리는 PC 혹은 스마트폰으로 근처의 맛집이나 명소들을 찾게 되죠. 하지만 이 기술 문명으로 얻게된 수많은 정보들이 오히려 우리에겐 새로운 유형의 선택 장애를 일으키곤 하죠. 또한 신빙성이 떨어지는 정보들과 무수히 많은 상업적 광고들을 접하게 되죠. 이 고민들을 해결하기 위해 직접 현재 자신의 위치 기반으로 본인이 원하는 카테고리에 알맞게 깊은 고민을 하지 않아도 되는 좋은 친구같은 서비스를 만들었습니다.

<h3>안녕하세요. 우리 동네입니다.</h3>

## How To Use

### [[우리 동네 바로가기]](https://www.neighbor.tk/)

- **AWS 비용 문제로 인하여 서버를 닫아놓은 상태입니다.**
- 또한 데이터를 꾸준하게 채우고 있으나 전반적으로 턱없이 부족한 상황입니다. 현재 거주지 주변의 데이터가 비교적 많으므로 위치 설정 아래와 같이 설정해주세요. 

---

<p align="center">[메인 화면 - 원하는 위치 설정 - 예시에 적힌 주소 (부산광역시 해운대구 좌동순환로)]</p>

---

## Tech Stack

<p align="center"> 
    <a href="https://www.java.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="50" height="50"/> </a> 
    <a href="https://spring.io/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" alt="spring" width="50" height="50"/> </a> 
    	<a href="https://www.w3.org/html/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/html5/html5-original-wordmark.svg" alt="html5" width="50" height="50"/> </a> 
	<a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/javascript/javascript-original.svg" alt="javascript" width="50" height="50"/> </a>
    	<a href="https://www.w3schools.com/css/" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/css3/css3-original-wordmark.svg" alt="css3" width="50" height="50"/> </a> 
	<a href="https://getbootstrap.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/bootstrap/bootstrap-plain-wordmark.svg" alt="bootstrap" width="50" height="50"/> </a> 
	<a href="https://git-scm.com/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/git-scm/git-scm-icon.svg" alt="git" width="50" height="50"/> </a>
    	<a href="https://aws.amazon.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/amazonwebservices/amazonwebservices-original-wordmark.svg" alt="aws" width="50" height="50"/> </a> 
	<a href="https://mariadb.org/" target="_blank" rel="noreferrer"> <img src="https://www.vectorlogo.zone/logos/mariadb/mariadb-icon.svg" alt="mariadb" width="50" height="50"/> </a> </p>

## 시스템 아키텍쳐

<p align="center"><img src="https://user-images.githubusercontent.com/97505799/208282507-d150b044-f898-4076-94c8-ea408dbc7845.PNG" alt="시스템 아키텍쳐" width="100%"></p>

## ERD

<p align="center"><img src="https://user-images.githubusercontent.com/97505799/208287746-5088d14c-8330-479f-82d9-530411d41b2c.png" alt="ERD" width="100%"></p>

## 핵심 기능

- 현재 위치를 기반으로 주변의 핫플레이스를 추천합니다.
  - 등록된 카테고리를 기반으로 핫플레이스 추천
  - 날씨 정보를 기반으로 핫플레이스 추천
  - 선택이 어려운 경우를 위해 무작위 랜덤 핫플레이스 추천
  - 직접 핫플레이스 검색 가능

## 구현 예정 기능
- 데이터 기반으로 원하는 핫플레이스를 추천합니다.
- 시간대별 핫플레이스를 추천합니다.
- 회원님의 나이, 성별에 맞춰진 핫플레이스를 추천합니다.
