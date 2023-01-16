const std = @import("std");
const fs = std.fs;

// 498,4 -> 498,6 -> 496,6
// 503,4 -> 502,4 -> 502,9 -> 494,9

pub fn main() !void {
    const buf = try read("14.txt");
    std.debug.print("{d}\n{s}\n", .{buf.len, buf});

    // into data structure
    // simulate algorithm
    // tests?
}

fn read(fname: []const u8) ![]u8 {
    const fd = try fs.cwd().openFile(fname, .{});
    defer fd.close();

    var buf: [1_000_000]u8 = undefined;
    const read_bytes = try fd.readAll(&buf);

    return buf[0..read_bytes];
}