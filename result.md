#### Описание тестов
Тесты проводились при помощи lua-скриптов и wrk.  
Всего доступны 4 конфигурации сервера:
  1) Базовая
  2) Базовая + setExecutor
  3) С кэшем
  4) С кэшем + setExecutor  

Цель тестирования - понять, какая конфигурация оптимальна для работы сервера, описанной в тесте realWorkModel.
#### Логи тестирования
1) FOR return new MyService(port, new MyDAO(data), false);
```
wrk --latency -c4 -d5m -s realWorkModel.lua http://localhost:8080
Running 5m test @ http://localhost:8080
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     4.00ms    9.94ms 314.49ms   97.16%
    Req/Sec   420.30    122.05   757.00     72.56%
  Latency Distribution
     50%    2.59ms
     75%    4.90ms
     90%    6.67ms
     99%   43.30ms
  250013 requests in 5.00m, 113.32MB read
Requests/sec:    833.11
Transfer/sec:    386.66KB
```
2) FOR  return new MyService(port, new MyDAO(data), true);
```
wrk --latency -c4 -d5m -s realWorkModel.lua http://localhost:8080
Running 5m test @ http://localhost:8080
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.91ms    9.22ms 179.48ms   97.82%
    Req/Sec   509.69    148.60   770.00     54.51%
  Latency Distribution
     50%    2.70ms
     75%    4.84ms
     90%    6.25ms
     99%   46.48ms
  303879 requests in 5.00m, 186.65MB read
Requests/sec:   1012.60
Transfer/sec:    636.91KB
```
3) FOR return new MyCacheService(port, new MyCacheDAO(data), false);
```
wrk --latency -c4 -d5m -s realWorkModel.lua http://localhost:8080
Running 5m test @ http://localhost:8080
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.61ms   14.43ms 466.59ms   98.32%
    Req/Sec   414.47     80.57   800.00     82.57%
  Latency Distribution
     50%    1.25ms
     75%    3.99ms
     90%    5.52ms
     99%   42.65ms
  246016 requests in 5.00m, 166.09MB read
Requests/sec:    819.77
Transfer/sec:    566.72KB
```
4) FOR return new MyCacheService(port, new MyCacheDAO(data), true);
```
wrk —latency -c4 -d5m -s realWorkModel.lua http://localhost:8080 
Running 5m test @ http://localhost:8080 
  2 threads and 4 connections 
  Thread Stats   Avg      Stdev     Max   +/- Stdev 
    Latency    12.79ms   85.29ms   1.78s    98.30% 
    Req/Sec   531.65    113.05     1.15k    82.79% 
  Latency Distribution 
    50% 2.11ms 
    75% 6.14ms 
    90% 9.27ms 
    99% 215.92ms 
  312001 requests in 5.00m, 182.14MB read 
Non-2xx or 3xx responses: 3 
Requests/sec: 1039.80 
Transfer/sec: 621.59KB
```
#### Вывод
В тесте realWorkModel каждая третья операция - чтение файла, который заведомо находится в кэше.
Однако, по результатам тестов нельзя сказать, что наличие кэша улучшило работу сервера. 
Можно сделать вывод, что для данного сервера с присущими ему параметрами 
(средний размер файла, количество файлов на сервере, приходящие запросы...) добавление кэша не даёт преимуществ.  
С другой стороны, установка Executor'а в обоих случаях (с кэшем и без него) помогла повысить производительность сервера.
