local key = KEYS[1]
local ttlSeconds = tonumber(ARGV[1])

if redis.call("exists", key) == 1 then
    return 0
end

redis.call("set", key, "1", "EX", ttlSeconds)
return 1
