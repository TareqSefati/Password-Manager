# 🔐 Password Manager Desktop Application
### Secure. Private. Yours.
A personal password manager built with **JavaFX** and **MongoDB**, designed to give you **full control** over your credentials — no third-party storage, no tracking, no compromise.

## 🌟 Overview
The **Password Manager Desktop Application** is a secure, self-hosted solution to store and manage your passwords locally or on your own MongoDB server.
It provides a clean and intuitive interface to organize credentials for websites, applications, and services — all in one place.

**This project emphasizes:**
- **Data Privacy** – Your passwords never leave your control.
- **Cross-device** Accessibility – Access your encrypted data from any system connected to your own MongoDB database.
- **Simple & Elegant UI** – Minimal and modern interface built with JavaFX.
- **Customizable** & Extensible – Fully open-source. Modify it as you wish.

## 🎯 Objectives
- Develop a secure personal password manager without relying on third-party services.
- Store and retrieve passwords from a centralized MongoDB database.
- Ensure easy access across devices through your self-hosted database.
- Keep complete ownership and control of your data.

## 🧰 Technologies Used
| Technology   | Purpose                                                  |
| ------------ | -------------------------------------------------------- |
| **JavaFX**   | User interface & client-side application                 |
| **MongoDB**  | Data persistence layer for storing encrypted credentials |
| **Maven**    | Build automation and dependency management               |
| **Java 17+** | Core programming language                                |

## 🚀 Features
- ✅ **User Authentication** – Secure login and registration.
- ✅ **MongoDB URI Configuration** – Connect your own database instance.
- ✅ **Password Management** – Add, edit, delete, and view encrypted credentials.
- ✅ **Dashboard Overview** – Quick access to all stored accounts.
- ✅ **Cross-platform Support** – Works on Windows, macOS, and Linux.
- ✅ **Modular Architecture** – Easy to extend and integrate with new modules.

## ⚙️ Installation & Setup
- 1️⃣ Clone the Repository
- 2️⃣ Build and Run
  - `mvn clean install`
  - `mvn javafx:run`

## 🔐 Security Notes
- All credentials are encrypted before being stored in MongoDB.
- The database connection is configurable by the user — no hardcoded URLs or credentials.
- Sensitive data is handled locally; nothing is transmitted externally.

## 🛠️ Future Enhancements
- 🔒 End-to-end AES encryption with user-specific keys.
- ☁️ Optional cloud backup using your own server.
- 🔑 Password generator & strength meter.
- 🧭 Search and tag-based password organization.
- 📱 Companion mobile app for on-the-go access.

## 🤝 Contribution
You are welcome to update, modify, or extend this project as per your needs!
If you'd like to collaborate or contribute improvements, feel free to:
- Fork the repository
- Create a feature branch
- Submit a pull request

### `Build your own password manager — because your privacy deserves your control. 🛡️`

### Screenshots:
![img-login-ui.png](src%2Fmain%2Fresources%2FREADME_Screenshots%2Fimg-login-ui.png)
![img-user-registration-ui.png](src%2Fmain%2Fresources%2FREADME_Screenshots%2Fimg-user-registration-ui.png)
![img-set-mongodb-uri.png](src%2Fmain%2Fresources%2FREADME_Screenshots%2Fimg-set-mongodb-uri.png)
![img-dashboard-ui.png](src%2Fmain%2Fresources%2FREADME_Screenshots%2Fimg-dashboard-ui.png)
![img-add-entry-ui.png](src%2Fmain%2Fresources%2FREADME_Screenshots%2Fimg-add-entry-ui.png)