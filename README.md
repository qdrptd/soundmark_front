# SoundMark

SoundMark는 사용자가 특정 위치에 음악을 "사운드 마크"로 남기고 다른 사용자와 공유할 수 있는 위치 기반 소셜 뮤직 앱입니다. 사용자들은 지도를 탐색하며 다른 사람들이 남긴 음악을 발견하고, 자신의 음악 취향을 공유하며 소통할 수 있습니다.

## 🚀 주요 기술 스택 및 라이브러리

이 프로젝트는 다음과 같은 최신 Android 기술 스택을 활용하여 개발되었습니다.

-   **언어**:
    -   [Kotlin](https://kotlinlang.org/): 전체 애플리케이션의 기본 개발 언어로, 간결하고 안정적인 코드 작성을 지원합니다.

-   **UI**:
    -   [Jetpack Compose](https://developer.android.com/jetpack/compose): 선언형 UI 툴킷을 사용하여 현대적이고 반응성이 뛰어난 UI를 구축합니다.
    -   [Material 3](https://m3.material.io/): 구글의 최신 디자인 시스템을 적용하여 일관되고 미려한 UI/UX를 제공합니다.
    -   [Coil](https://coil-kt.github.io/coil/): 코루틴 기반의 이미지 로딩 라이브러리로, 앨범 커버 등 네트워크 이미지를 효율적으로 처리합니다.

-   **지도 및 위치**:
    -   [Google Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk): 핵심 기능인 지도 표시, 마커, 클러스터링 등을 구현합니다.
    -   [Maps Compose](https://developers.google.com/maps/documentation/android-sdk/maps-compose): Jetpack Compose 환경에서 Google 지도를 손쉽게 통합하고 제어합니다.
    -   [Google Play Services Location](https://developers.google.com/android/reference/com/google/android/gms/location/package-summary): 사용자의 현재 위치를 정확하게 가져오기 위해 사용됩니다.

-   **네트워킹**:
    -   [Retrofit](https://square.github.io/retrofit/): HTTP 통신을 위한 라이브러리로, 서버 API와 효율적으로 통신합니다.
    -   [OkHttp](https://square.github.io/okhttp/): Retrofit의 하부에서 실제 HTTP 요청을 처리하며, 로깅 인터셉터 등을 통해 디버깅을 지원합니다.
    -   [Gson](https://github.com/google/gson): JSON 데이터를 Kotlin 데이터 클래스로 직렬화/역직렬화하는 데 사용됩니다.

-   **아키텍처 및 DI**:
    -   **MVVM (Model-View-ViewModel)**: UI와 비즈니스 로직을 분리하여 테스트와 유지보수가 용이한 구조를 채택했습니다.
    -   [Hilt (Dagger)](https://developer.android.com/training/dependency-injection/hilt-android): 의존성 주입(DI) 프레임워크로, 객체 간의 의존성을 관리하고 코드의 모듈성을 높입니다.

-   **비동기 처리**:
    -   [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-guide.html): 네트워크 요청, 데이터베이스 접근 등 비동기 작업을 효율적이고 간결하게 처리합니다.
    -   [Flow](https://kotlinlang.org/docs/flow.html): Coroutines의 일부로, 데이터 스트림을 비동기적으로 처리하여 UI 상태를 실시간으로 업데이트합니다.

-   **내비게이션**:
    -   [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation): Compose 기반의 화면 전환을 관리하며, `NavigationDestination` 열거형을 통해 라우트를 중앙에서 관리합니다.

## 🏛️ 프로젝트 구조

프로젝트는 기능별로 패키지를 나누어 관리하며, 주요 구조는 다음과 같습니다.

-   `app/src/main/java/com/example/soundmark`: 애플리케이션의 메인 소스 코드가 위치합니다.
    -   `di`: Hilt를 사용한 의존성 주입 모듈들이 정의된 패키지입니다. (e.g., `NetworkModule`, `RepositoryModule`)
    -   `data`: 데이터 처리를 담당하는 클래스들이 위치합니다.
        -   `model`: `SoundMark`, `Track`, `User` 등 애플리케이션에서 사용하는 데이터 모델 클래스.
        -   `dto`: 서버와 통신할 때 사용하는 Data Transfer Object.
        -   `network`: Retrofit 인터페이스 및 API 관련 클래스.
        -   `repository`: 데이터 소스(네트워크, 로컬)를 추상화하는 저장소 패턴 구현체.
    -   `ui`: UI와 관련된 모든 컴포저블 함수 및 ViewModel이 위치합니다.
        -   `views`: `HomeScreen`, `ProfileScreen` 등 각 화면(Screen) 단위의 컴포저블 및 관련 UI 요소.
        -   `theme`: 앱의 전체적인 색상, 타이포그래피 등 디자인 시스템 정의.
    -   `util`: 로깅, 상수 관리 등 프로젝트 전반에서 사용되는 유틸리티 클래스.
-   `app/build.gradle.kts`: 프로젝트의 의존성 및 빌드 설정이 정의된 파일입니다.

## ⚙️ 동작 방식

1.  **앱 실행 및 온보딩**: 사용자가 앱을 처음 실행하면 `SplashScreen`을 거쳐 온보딩 화면으로 안내됩니다. 온보딩이 완료되면 메인 화면으로 이동합니다.

2.  **홈 화면 (지도)**:
    -   `HomeScreen`에서는 Google 지도를 표시합니다.
    -   `HomeViewModel`은 `SoundMarkRepository`를 통해 서버에서 사운드 마크 데이터를 가져옵니다.
    -   가져온 사운드 마크들은 지도 상의 확대/축소 레벨에 따라 **클러스터링(Clustering)** 되어 표시됩니다. 클러스터는 `createClusterIcon` 함수를 통해 동적으로 생성된 비트맵 아이콘으로, 포함된 마크의 개수를 보여줍니다.
    -   사용자와의 거리에 따라 클러스터 및 마크는 활성/비활성 상태로 나뉩니다. 비활성 상태에서는 상세 정보를 볼 수 없습니다.

3.  **사운드 마크 상호작용**:
    -   활성화된 클러스터를 탭하면 `ModalBottomSheet`가 나타나 해당 위치에 있는 사운드 마크들의 목록(노래 제목, 아티스트 등)을 보여줍니다.
    -   목록에서 특정 항목을 선택하면 `SongDetailScreen`으로 이동하여 사운드 마크에 대한 상세 정보(메시지, 반응 등)를 확인할 수 있습니다.

4.  **사운드 마크 추가**:
    -   사용자는 지도 화면의 `FloatingActionButton`을 눌러 `SongAddScreen`으로 이동합니다.
    -   여기서 Spotify API 등을 통해 음악을 검색하고, 현재 위치에 메시지와 함께 새로운 사운드 마크를 생성할 수 있습니다.

5.  **내비게이션**:
    -   화면 간의 이동은 `NavigationDestination` 열거형 클래스에 정의된 라우트(route)를 기반으로 `NavHost`에 의해 관리됩니다. 이를 통해 모든 내비게이션 로직을 중앙에서 명확하게 파악할 수 있습니다.

6.  **데이터 흐름**:
    -   **View (`Composable`)** -> **ViewModel** -> **Repository** -> **(Network/Local DataSource)**
    -   UI(View)는 사용자의 입력을 `ViewModel`에 전달합니다.
    -   `ViewModel`은 비즈니스 로직을 처리하고, `Repository`에 데이터 처리를 요청합니다.
    -   `Repository`는 Retrofit을 통해 서버 API를 호출하거나, 필요시 로컬 데이터베이스에서 데이터를 가져옵니다.
    -   데이터 변경은 `Flow`나 `StateFlow`를 통해 UI에 전달되어 화면이 비동기적으로 업데이트됩니다.
