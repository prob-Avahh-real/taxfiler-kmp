import SwiftUI
import shared

@main
struct TaxFilerApp: App {
    init() {
        Task {
            do {
                try await shared.TaxFilerHttpClient.shared.warmup()
                print("[TaxFiler] 预热完成")
            } catch {
                print("[TaxFiler] 预热失败 (可忽略): \(error)")
            }
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
