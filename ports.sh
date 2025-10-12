for port in 9000 8090 8092 8093 8089 9090 8088 8086 8085 8087 8080 8079; do
  pid=$(lsof -ti tcp:$port)
  if [ -n "$pid" ]; then
    echo "Killing process $pid on port $port"
    kill -9 $pid
  else
    echo "Port $port is free"
  fi
done
