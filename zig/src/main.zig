const std = @import("std");
const print = std.debug.print;
const mem = std.mem;
const fs = std.fs;

// 498,4 -> 498,6 -> 496,6
// 503,4 -> 502,4 -> 502,9 -> 494,9

pub fn main() !void {
    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    const allocator = gpa.allocator();
    defer _ = gpa.deinit();

    const buf = try read(allocator, "14.txt");
    defer allocator.free(buf);

    // Split lines.
    var lines = std.ArrayList([]const u8).init(allocator);
    var s: u32 = 0;
    var i: u32 = 0;
    while (i < buf.len) : (i += 1) { // for loop?
        if (buf[i] != '\n') {
            continue;
        }

        var l = try allocator.alloc(u8, i - 1 - s);
        mem.copy(u8, l, buf[s..(i - 1)]);
        try lines.append(l);
        s = i + 1;
        continue;
    }
    var l = try allocator.alloc(u8, i - 1 - s);
    mem.copy(u8, l, buf[s..(i - 1)]);
    try lines.append(l);

    for (lines.items) |line| {
        print("Line: {s}\n", .{line});
    }

    for (lines.items) |line| {
        allocator.free(line);
    }
    lines.clearAndFree();

    // into data structure
    // simulate algorithm
    // tests?
}

// Returned value has to be free'd by caller.
fn read(allocator: std.mem.Allocator, fname: []const u8) ![]u8 {
    const fd = try fs.cwd().openFile(fname, .{});
    defer fd.close();
    var size = (try fd.stat()).size;

    var buf: []u8 = allocator.alloc(u8, size) catch {
        _ = std.io.getStdErr().write("Unable to allocate memory to read file") catch {
            // Nothing we can really do here.
        };
        std.os.exit(1);
    };
    _ = try fd.readAll(buf);

    return buf;
}
