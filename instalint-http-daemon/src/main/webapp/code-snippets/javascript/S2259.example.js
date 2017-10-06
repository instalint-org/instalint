// Properties of variables with "null" or "undefined" values should not be accessed

function RadialAxial_getPattern(ctx) {
    var grad;
    if (type === 'axial') {
      grad = ctx.createLinearGradient(p0[0], p0[1], p1[0], p1[1]);
    } else if (type === 'radial') {
      grad = ctx.createRadialGradient(p0[0], p0[1], r0, p1[0], p1[1], r1);
    }

    for (var i = 0, ii = colorStops.length; i < ii; ++i) {
      var c = colorStops[i];
      grad.addColorStop(c[0], c[1]);
    }

    return grad;
}
