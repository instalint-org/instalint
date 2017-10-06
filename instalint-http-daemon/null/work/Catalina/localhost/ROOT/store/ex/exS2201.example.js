// Return values from functions without side effects should not be ignored

function _roundMinAndMax(max, min, roundingUnit, alwaysShowZero) {

    if(alwaysShowZero || max === 0 || max + roundingUnit > 0)
    {
        max = 0;
        roundingUnit = (max - min)/units;
        if(useIntegers)
        {
            Math.ceil(roundingUnit);
            return max - (roundingUnit * units);
        }
        else
        {
            return max - Math.ceil(roundingUnit * units * 100000)/100000;
        }
    }

    return 0;
}
