const std = @import("std");
const mem = std.mem;
const fs = std.fs;
const print = std.debug.print;
const util = @import("util");

// 498,4 -> 498,6 -> 496,6
// 503,4 -> 502,4 -> 502,9 -> 494,9

const sand_source = Point{ .x = 500, .y = 0 };

const Point = struct {
    x: i32,
    y: i32,

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
            .x = std.fmt.parseInt(i32, parts[0], 10) catch undefined,
            .y = std.fmt.parseInt(i32, parts[1], 10) catch undefined,
        };
    }
};

// Day 14.
pub fn main() !void {
    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    const allocator = gpa.allocator();
    defer _ = gpa.deinit();

    var walls = std.AutoHashMap(Point, void).init(allocator);
    defer walls.deinit();

    var sand = std.AutoHashMap(Point, void).init(allocator);
    defer sand.deinit();

    var lines = try readLinesFromFile(allocator, "14.txt");
    defer freeSlice(allocator, lines);
    for (lines) |line| {
        parseLine(allocator, &walls, line) catch {
            print("Unable to parse line {s}\n", .{line});
            return;
        };
    }

    print("LEVEL\n", .{});
    draw(&walls, &sand);

    var steps: u32 = 10;
    while (steps > 0): (steps -= 1) {
        try simulate(&walls, &sand);
        // var si = sand.keyIterator();
        // while (si.next()) |s| {
        //     print("S: {}\n", .{s});
        // }
        draw(&walls, &sand);
    }
}

fn simulate(walls: *std.AutoHashMap(Point, void), sand: *std.AutoHashMap(Point, void)) !void {
    var pos = Point{ .x = 500, .y = 0 };

    while (true) {
        var move = nextMove(pos, walls, sand);
        if (move) |np| {
            pos = np;
        } else {
            break;
        }
    }

    try sand.put(pos, {});
}

fn nextMove(pos: Point, walls: *std.AutoHashMap(Point, void), sand: *std.AutoHashMap(Point, void)) ?Point {
    // Down?
    var down = Point{ .x = pos.x, .y = pos.y + 1 };
    if (!walls.contains(down) and !sand.contains(down)) {
        return down;
    }

    var down_left = Point{ .x = pos.x - 1, .y = pos.y + 1 };
    if (!walls.contains(down_left) and !sand.contains(down_left)) {
        return down_left;
    }

    var down_right = Point{ .x = pos.x + 1, .y = pos.y + 1 };
    if (!walls.contains(down_right) and !sand.contains(down_right)) {
        return down_right;
    }

    return null;
}

fn draw(points: *std.AutoHashMap(Point, void), sand: *std.AutoHashMap(Point, void)) void {
    var tmp = points.keyIterator();
    var start = tmp.next() orelse undefined;
    var top_left = Point{ .x = start.x, .y = start.y };
    var bottom_right = Point{ .x = start.x, .y = start.y };

    var ki = points.keyIterator();
    while (ki.next()) |p| {
        top_left.x = @min(top_left.x, p.x);
        top_left.y = @min(top_left.y, p.y);
        bottom_right.x = @max(bottom_right.x, p.x);
        bottom_right.y = @max(bottom_right.y, p.y);
    }

    const border = 1;
    var y = top_left.y - border;
    while (y < bottom_right.y + border + 1) : (y += 1) {
        var x = top_left.x - border;
        while (x < bottom_right.x + border + 1) : (x += 1) {
            if (points.contains(Point{ .x = x, .y = y })) {
                print("#", .{});
            } else if (sand.contains(Point{ .x = x, .y = y })) {
                print("o", .{});
            } else {
                print(".", .{});
            }
        }
        print("\n", .{});
    }
}

fn parseLine(allocator: std.mem.Allocator, points: *std.AutoHashMap(Point, void), line: []const u8) !void {
    // print("Parsing '{s}'\n", .{line});
    var segments = try split(allocator, line, " -> ");
    defer freeSlice(allocator, segments);

    var plist = std.ArrayList(Point).init(allocator);
    for (segments) |segment| {
        try plist.append(Point.parse(allocator, segment));
    }

    var ps = plist.toOwnedSlice();
    defer allocator.free(ps);

    var i: u32 = 0;
    while (i < ps.len - 1) : (i += 1) {
        try addPoints(points, ps[i], ps[i + 1]);
    }
}

fn addPoints(points: *std.AutoHashMap(Point, void), p1: Point, p2: Point) !void {
    if (p1.x == p2.x) {
        // Iterate over y
        if (p1.y < p2.y) {
            var y = p1.y;
            while (y <= p2.y) : (y += 1) {
                try points.put(Point{ .x = p1.x, .y = y }, {});
            }
        } else {
            var y = p2.y;
            while (y <= p1.y) : (y += 1) {
                try points.put(Point{ .x = p1.x, .y = y }, {});
            }
        }
    } else {
        // Iterate over x
        if (p1.x < p2.x) {
            var x = p1.x;
            while (x <= p2.x) : (x += 1) {
                try points.put(Point{ .x = x, .y = p1.y }, {});
            }
        } else {
            var x = p2.x;
            while (x <= p1.x) : (x += 1) {
                try points.put(Point{ .x = x, .y = p1.y }, {});
            }
        }
    }
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
    return s[i .. j + 1];
}
