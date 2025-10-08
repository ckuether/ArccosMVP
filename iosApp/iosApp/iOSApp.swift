import SwiftUI
import GoogleMaps

@main
struct iOSApp: App {
    init() {
        // Initialize Google Maps with API key
        GMSServices.provideAPIKey("AIzaSyC8T8-T2cRGeVnevULekJhAtk3lBmh72eI")  // Temporarily commented out
        print("iOS App initializing...")
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}