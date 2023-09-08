# Giới thiệu

App real-time chat hỗ trợ các chức năng cơ bản của một app chat bao gồm:
1. Đăng ký tài khoản, khởi tạo và chỉnh sửa thông tin cá nhân
2. Trò chuyện thời gian thực
3. Thông báo tin nhắn trong thòi gian thực
4. Hiển thị lịch sử tn nhắni

App được xây dựng bằng framework Android SDK, Ngôn ngữ sử dụng: Java. 

Database sử dụng: MySQL. App truy vấn thông tin thông qua REST request(sử dụng retrofit để trừu tượng hóa quá trình gửi request) và sử dụng WebSocket để gửi/nhận tin nhắn.

[Android SDK](https://vi.wikipedia.org/wiki/Android_SDK)  
[Retrofit](https://square.github.io/retrofit/)  
[WebSocket](https://en.wikipedia.org/wiki/WebSocket)  
[WebSocket Client](https://github.com/TakahikoKawasaki/nv-websocket-client)  

# Hướng dẫn chạy project
Yêu cầu cài sẵn Android studio để chạy thử project.
# Thư viện bổ trợ
##### Các thư viện đã add vào file build.gradle(module:DormManagement):
* implementation 'com.github.MikeOrtiz:TouchImageView:1.4.1'
* implementation "androidx.room:room-runtime:$room_version"
* annotationProcessor "androidx.room:room-compiler:$room_version"
* implementation 'com.github.bumptech.glide:glide:4.14.2')
* implementation 'com.cloudinary:cloudinary-android:2.3.1'
* implementation group: 'commons-io', name: 'commons-io', version: '2.11.0'
* implementation "androidx.camera:camera-core:${camerax_version}" ()
* implementation "androidx.camera:camera-camera2:${camerax_version}"
* implementation "androidx.camera:camera-lifecycle:${camerax_version}"
* implementation "androidx.camera:camera-view:${camerax_version}"
* implementation 'com.squareup.retrofit2:retrofit:2.9.0'
* implementation 'com.squareup.retrofit2:retrofit:2.9.0'
* implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
* implementation group: 'com.squareup.okhttp3', name: 'okhttp', version: '3.2.0'
* implementation 'com.github.NaikSoftware:StompProtocolAndroid:1.6.6'
* implementation group: 'io.reactivex.rxjava2', name: 'rxjava', version: '2.2.21'

# Cấu trúc project
* Activities: package lưu các activity chính trong app.
* Adapters: package lưu các adapter của recyclerView.
* Dialogs : package chứa các pop-up dialog.
* Models: package chứa các Viewmodel hỗ trợ lưu trữ các trạng thái của fragment và activity
* Objects: package chứa các data model chia làm ba loại: entity(object tượng trưng bảng trong SQLite), DTO(object chứa nội dung hiển th trong các recyclerView) và các object khác
* Fragments: package chứa các fragment hỗ trợ đa dạng các trang tương tác.
* Interfaces: package chứa các lớp giao diện được sử dụng như callback bên trong các lớp Adapter
* Services : Các service thực hiện REST request
* Dtos : Các lớp đối tượng chứa thông tin của app.
* Entities: Các lớp đối tượng tương ứng với bảng trong database  

Testing account: Duc-123 | Phuc-123
