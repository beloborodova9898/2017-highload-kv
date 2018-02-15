math.randomseed(os.time())

c = 0
ind0_100 = 0

request = function()
    local cmd = "/v0/entity?id=" .. c
    local data
    local ind100_200 = ind0_100 + 100

    -- Записываем первые 100 файлов, они будут постоянно требоваться и не перезаписываться
    if (cnt < 100) then
        wrk.method = "PUT"
        data = ""
        for i = 1, math.random(1, 500) do
            data = data .. math.random(1, 9999)
        end
        wrk.body = data
    end

    -- Следующие 100 файлов будут перезаписываться
    if(cnt > 99) then
        if (cnt % 3 == 0) then -- Запись файла 100-200
            wrk.method = "PUT"
            data = ""
            for i = 1, math.random(1, 500) do
                data = data .. math.random(1, 9999)
            end
            wrk.body = data

            cmd = "/v0/entity?id=" .. ind100_200
        end

        if (cnt % 3 == 1) then -- Чтение файла 100-200
            wrk.method = "GET"
            cmd = "/v0/entity?id=" .. ind100_200
        end

        if (cnt % 3 == 2) then -- Чтение файла 0-100
            wrk.method = "GET"
            cmd = "/v0/entity?id=" .. ind0_100
        end
    end

    c = c + 1;
    ind0_100 = ind0_100 % 100

    return wrk.format(method, cmd, nil, body)
end
