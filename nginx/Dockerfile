FROM nginx:alpine

# Remove default config
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/

# Create error page
RUN echo '<!DOCTYPE html><html><head><title>Service Unavailable</title></head><body><h1>Service temporarily unavailable</h1><p>Please try again later.</p></body></html>' > /usr/share/nginx/html/50x.html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
