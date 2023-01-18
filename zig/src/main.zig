const std = @import("std");
const mem = std.mem;
const fs = std.fs;
const print = std.debug.print;
const util = @import("util");

// 498,4 -> 498,6 -> 496,6
// 503,4 -> 502,4 -> 502,9 -> 494,9

pub fn main() !void {
    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    const allocator = gpa.allocator();
    defer _ = gpa.deinit();

    var lines = try readLinesFromFile(allocator, "14.txt");
    for (lines) |line| {
        print("{s}\n", .{line});
    }

    // into data structure
    // simulate algorithm
    // tests?
    freeSlice(allocator, lines);
}

fn freeSlice(allocator: std.mem.Allocator, slice: [][]const u8) void {
    for (slice) |line| {
        allocator.free(line);
    }
    allocator.free(slice);
}

fn readLinesFromFile(allocator: std.mem.Allocator, filename: []const u8) ![][]const u8 {
    const buf = try readFromFile(allocator, filename);
    defer allocator.free(buf);
    return try splitString(allocator, buf);
}

// Returned value has to be free'd by caller.
fn readFromFile(allocator: std.mem.Allocator, fname: []const u8) ![]u8 {
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

fn splitString(allocator: std.mem.Allocator, string: []u8) ![][]const u8 {
    var lines = std.ArrayList([]const u8).init(allocator);
    defer lines.deinit();
    var s: u32 = 0;
    var i: u32 = 0;
    while (i < string.len) : (i += 1) { // for loop?
        if (string[i] != '\n') {
            continue;
        }

        var l = try allocator.alloc(u8, i - s);
        mem.copy(u8, l, string[s..i]);
        try lines.append(l);
        s = i + 1;
        continue;
    }

    var l = try allocator.alloc(u8, i - s);
    mem.copy(u8, l, string[s..i]);
    try lines.append(l);

    return lines.toOwnedSlice();
}