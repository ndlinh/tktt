# Yêu cầu:

- Java 1.8 hoặc 1.9
- MongoDB

# Các bước build chương trình

##1. Cài đặt `maven`

- Download maven: https://maven.apache.org/download.cgi
- Giải nén và đặt biến môi trường PATH chỉ vào thư mục `bin` của maven

##2. Tải source code:

Tải source code tại https://github.com/ndlinh/tktt

##3. Build chương trình

Sử dụng Command Line, chuyển vào thư mục source code. Sau đó build bằng lệnh sau:

    mvn package
    
##3. Chạy chương trình (Chưa hoàn thiện)

- Nếu sử dụng Java 1.9

    `java -jar --add-modules=java.xml.bind target/tktt-1.0-SNAPSHOT.jar  -p <path to document directory>`
    

- Nếu sử dụng Java 1.8     

    `java -jar target/tktt-1.0-SNAPSHOT.jar -p <path to document directory>`
    


