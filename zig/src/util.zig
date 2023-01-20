const std = @import("std");

pub fn printType(t: anytype) void {
    std.debug.print("{}\n", .{@TypeOf(t)});
}
