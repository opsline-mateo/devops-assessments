#!/usr/bin/env python3
import http.server
import socketserver
import os
import sys
from datetime import datetime

class CustomHandler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        if self.path == '/':
            self.path = '/index.html'
        elif self.path == '/health':
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            health_data = {
                "status": "healthy",
                "timestamp": datetime.now().isoformat(),
                "architecture": "amd64",
                "version": "1.0.0"
            }
            self.wfile.write(str(health_data).encode())
            return
        elif self.path == '/info':
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.end_headers()
            info_data = {
                "app": "Simple Web App",
                "architecture": "amd64",
                "python_version": sys.version,
                "platform": sys.platform,
                "working_directory": os.getcwd()
            }
            self.wfile.write(str(info_data).encode())
            return
        
        return super().do_GET()

    def log_message(self, format, *args):
        print(f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}] {format % args}")

if __name__ == "__main__":
    PORT = int(os.environ.get('PORT', 8080))
    
    print(f"Starting Simple Web App on port {PORT}")
    print(f"Architecture: amd64")
    print(f"Python version: {sys.version}")
    
    with socketserver.TCPServer(("", PORT), CustomHandler) as httpd:
        print(f"Server running at http://0.0.0.0:{PORT}/")
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nShutting down server...")
            httpd.shutdown()
