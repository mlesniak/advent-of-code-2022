const std = @import("std");
const mem = std.mem;
const fs = std.fs;
const print = std.debug.print;
const util = @import("util");

// 498,4 -> 498,6 -> 496,6
// 503,4 -> 502,4 -> 502,9 -> 494,9

const Point = struct {
    x: u32,
    y: u32,

    // Parse a string '123,456' as a point. No error handling.
    fn parse(allocator: std.mem.Allocator, s: []const u8) Point {
        var trimmed = trim(s);
        var parts = split(allocator, trimmed, ",") catch undefined;
        defer {
            for (parts) |p| {
                allocator.free(p);
            }
            allocator.free(parts);
        }
        return Point{
            .x = std.fmt.parseInt(u32, parts[0], 10) catch undefined,
            .y = std.fmt.parseInt(u32, parts[1], 10) catch undefined,
        };
    }
};

// Day 14.
pub fn main() !void {
    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    const allocator = gpa.allocator();
    defer _ = gpa.deinit();

    var p = Point.parse(allocator, "    123,456              ");
    print("{}\n", .{p});

    // var lines = try readLinesFromFile(allocator, "14.txt");
    // defer freeSlice(allocator, lines);
    // for (lines) |line| {
    //     print("'{s}'\n", .{line});
    // }

    // var points = std.AutoHashMap(Point, void).init(allocator);
    // _ = points;

    // into data structure
    // simulate algorithm
    // tests?
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
    return try split(allocator, buf, "\n");
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

fn split(allocator: std.mem.Allocator, string: []const u8, separator: []const u8) ![][]u8 {
    var lines = std.ArrayList([]u8).init(allocator);
    defer lines.deinit();

    var s: u32 = 0;
    var i: u32 = 0;
    loop: while (i < string.len) : (i += 1) { // for loop?
        for (separator) |c, si| {
            if (i + si > string.len or string[i + si] != c) {
                continue :loop;
            }
        }

        var l = try allocator.alloc(u8, i - s);
        mem.copy(u8, l, string[s..i]);
        try lines.append(l);
        s = i + @intCast(u32, separator.len);
    }
    var l = try allocator.alloc(u8, i - s);
    mem.copy(u8, l, string[s..i]);
    try lines.append(l);

    return lines.toOwnedSlice();
}

fn trim(s: []const u8) []const u8 {
    var i: u32 = 0;
    while (s[i] == ' ') : (i += 1) {}
    var j = s.len - 1;
    while (s[j] == ' ') : (j -= 1) {}
    return s[i..j+1];
}
