wrk.method = "GET"
c = 0

request = function()
    local cmd = "/v0/entity?id=" .. c
    c = c + 1
    return wrk.format(nil, cmd)
end
