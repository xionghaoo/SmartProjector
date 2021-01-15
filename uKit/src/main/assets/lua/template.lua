
local function runBlockAgain()
    if (not(isRunning())) then return end
    restartProgram()
end

local function setServoInternal(...)
    setServo(string.format(...))
end

local function setServoInternalV2(...)
    setServo(string.format(...))
    sleep(select(2, ...))
end

local function setServoBySpeedPercentInternal(...)
    setServoBySpeedPercent(string.format(...))
end

local function setMotorSpeedInternal(...)
    setMotorSpeed(string.format(...))
end

local function setEmojiInternal(...)
    setEmoji(string.format(...))
end

local function setScenelightInternal(...)
    setScenelight(string.format(...))
end

local function setLEDsInternal(...)
    setLEDs(string.format(...))
end

local function setUltrasonicLEDInternal(...)
    setUltrasonicLED(string.format(...))
end

local function setGroupUltrasonicLEDInternal(...)
    setGroupUltrasonicLED(string.format(...))
end

local function restrictNumberRange(num, minValue, maxValue)
    return math.max(minValue, math.min(maxValue, num))
end

local function restrictMaxRange(num, maxValue)
    return math.min(maxValue, num)
end

local function restrictMinRange(num, minValue)
    return math.max(minValue, num)
end

function run()
    %s
end
