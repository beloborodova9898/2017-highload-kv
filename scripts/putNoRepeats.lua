math.randomseed(os.time())

wrk.method = "PUT"
c = 0

request = function()
    local cmd = "/v0/entity?id=" .. c

    local data = ""
    for i = 1, 1000 do
        data = data .. math.random(1, 9999)
    end

    wrk.body = data
    c = c + 1
    return wrk.format(nil, cmd)
end
