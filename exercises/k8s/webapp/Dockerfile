# Use Python 3.11 slim image as base (amd64)
FROM --platform=linux/amd64 python:3.11-slim

# Set working directory
WORKDIR /app

# Copy application files
COPY index.html .
COPY server.py .

# Make server.py executable
RUN chmod +x server.py

# Expose port
EXPOSE 8080

# Set environment variable
ENV PORT=8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD python3 -c "import urllib.request; urllib.request.urlopen('http://localhost:8080/health')"

# Run the application
CMD ["python3", "server.py"]
